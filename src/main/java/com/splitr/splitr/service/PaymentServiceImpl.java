package com.splitr.splitr.service;

import com.splitr.splitr.dto.CreatePaymentRequest;
import com.splitr.splitr.dto.PaymentDto;
import com.splitr.splitr.entity.ExpenseSplit;
import com.splitr.splitr.entity.Group;
import com.splitr.splitr.entity.Membership;
import com.splitr.splitr.entity.Payment;
import com.splitr.splitr.entity.User;
import com.splitr.splitr.repository.ExpenseSplitRepository;
import com.splitr.splitr.repository.GroupRepository;
import com.splitr.splitr.repository.MembershipRepository;
import com.splitr.splitr.repository.PaymentRepository;
import com.splitr.splitr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final GroupRepository groupRepo;
    private final MembershipRepository membershipRepo;
    private final UserRepository userRepo;
    private final PaymentRepository paymentRepo;
    private final ExpenseSplitRepository expenseSplitRepo;

    @Override
    @Transactional
    public PaymentDto recordPayment(Long groupId, CreatePaymentRequest req, User currentUser) {
        // 1) Grup kontrolü
        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grup bulunamadı: " + groupId));

        // 2) Sadece üyeler ödeme yapabilir
        membershipRepo.findByGroupAndUser(group, currentUser)
                .filter(Membership::isAccepted)
                .orElseThrow(() -> new SecurityException("Üye olmadığınız grupta ödeme yapamazsınız"));

        // 3) Receiver kullanıcı kodu ile bulunur
        User receiver = userRepo.findByUserCode(req.getReceiverUserCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Alacaklı kullanıcı bulunamadı: " + req.getReceiverUserCode()));

        // 4) Receiver'ın da grupta üyeliğini kontrol et
        membershipRepo.findByGroupAndUser(group, receiver)
                .filter(Membership::isAccepted)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Alacaklı kullanıcı grupta değil: " + req.getReceiverUserCode()));

        // 5) Payment nesnesini oluştur ve kaydet
        Payment payment = paymentRepo.save(Payment.builder()
                .group(group)
                .payer(currentUser)
                .receiver(receiver)
                .amount(req.getAmount())
                .paymentDate(LocalDateTime.now())
                .build()
        );

        // 6) Borç azaltma mantığı
        reduceDebtFromPayment(currentUser, receiver, group, req.getAmount());

        // 7) DTO'ya dönüştür ve döndür
        return new PaymentDto(
                payment.getId(),
                payment.getPayer().getUserCode(),
                payment.getReceiver().getUserCode(),
                payment.getAmount(),
                payment.getPaymentDate()
        );
    }

    @Override
    public List<PaymentDto> listPayments(Long groupId, User currentUser) {
        // 1) Grup kontrolü
        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grup bulunamadı: " + groupId));

        // 2) Üyelik kontrolü
        membershipRepo.findByGroupAndUser(group, currentUser)
                .filter(Membership::isAccepted)
                .orElseThrow(() -> new SecurityException("Bu grubun üyesi değilsiniz"));

        // 3) Ödemeleri çek ve DTO listesine çevir
        return paymentRepo.findByGroup(group).stream()
                .map(p -> new PaymentDto(
                        p.getId(),
                        p.getPayer().getUserCode(),
                        p.getReceiver().getUserCode(),
                        p.getAmount(),
                        p.getPaymentDate()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Ödeme sonrası borç azaltma mantığı
     * Ödeme miktarı kadar borç azaltılır, borç sıfırlanırsa kayıt silinir
     */
    private void reduceDebtFromPayment(User payer, User receiver, Group group, BigDecimal paymentAmount) {
        // Payer'ın receiver'a olan borçlarını bul
        List<ExpenseSplit> payerDebts = expenseSplitRepo.findByUserAndGroup(payer.getUserCode(), group.getId());
        
        BigDecimal remainingPayment = paymentAmount;
        
        for (ExpenseSplit debt : payerDebts) {
            if (remainingPayment.compareTo(BigDecimal.ZERO) <= 0) break;
            
            // Bu borç receiver'a mı?
            if (debt.getExpense().getCreatedBy().getUserCode().equals(receiver.getUserCode())) {
                BigDecimal debtAmount = debt.getAmountOwed();
                
                if (remainingPayment.compareTo(debtAmount) >= 0) {
                    // Ödeme borçtan fazla veya eşit - borcu tamamen sil
                    expenseSplitRepo.delete(debt);
                    remainingPayment = remainingPayment.subtract(debtAmount);
                } else {
                    // Ödeme borçtan az - borcu azalt
                    debt.setAmountOwed(debtAmount.subtract(remainingPayment));
                    expenseSplitRepo.save(debt);
                    remainingPayment = BigDecimal.ZERO;
                }
            }
        }
    }
}

