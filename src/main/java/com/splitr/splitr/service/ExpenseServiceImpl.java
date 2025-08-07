package com.splitr.splitr.service;

import com.splitr.splitr.dto.BalanceDto;
import com.splitr.splitr.dto.CreateExpenseRequest;
import com.splitr.splitr.dto.ExpenseDto;
import com.splitr.splitr.entity.*;
import com.splitr.splitr.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService{
    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    @Override
    @Transactional
    public ExpenseDto createExpense(Long groupId,
                                    CreateExpenseRequest req,
                                    User currentUser) {
        // 1) Grup varlığını kontrol et
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grup bulunamadı: " + groupId));

        // 2) Toplam tutarı ve paydaş sayısını al
        final BigDecimal totalAmount = req.getAmount();
        final int memberCount = req.getMemberUserCodes().size();

        // 3) Kişi başı düşen payı hesapla (scale=2, ROUND_HALF_UP)
        final BigDecimal share = totalAmount
                .divide(BigDecimal.valueOf(memberCount), 2, BigDecimal.ROUND_HALF_UP);

        // 4) Expense nesnesini oluştur ve kaydet (savedExpense artık final)
        final Expense savedExpense = expenseRepository.save(
                Expense.builder()
                        .title(req.getTitle())
                        .amount(totalAmount)
                        .createdBy(currentUser)
                        .group(group)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        // 5) Her üye kodu için ExpenseSplit nesnelerini oluştur
        final List<ExpenseSplit> splits = req.getMemberUserCodes().stream()
                .map(code -> {
                    // Kullanıcıyı userCode üzerinden bul
                    User member = userRepository.findByUserCode(code)
                            .orElseThrow(() -> new IllegalArgumentException("Kullanıcı yok: " + code));

                    // ExpenseSplit builder ile nesneyi kur
                    return ExpenseSplit.builder()
                            .expense(savedExpense)
                            .user(member)
                            .amountOwed(share)
                            .build();
                })
                .collect(Collectors.toList());

        // 6) Tüm split kayıtlarını tek seferde kaydet
        expenseSplitRepository.saveAll(splits);

        // 7) Oluşan Expense’i DTO’ya dönüştür ve dön
        return new ExpenseDto(
                savedExpense.getId(),
                savedExpense.getTitle(),
                savedExpense.getAmount(),
                savedExpense.getCreatedBy().getUserCode(),
                savedExpense.getCreatedAt()
        );
    }

    @Override
    public List<ExpenseDto> listExpenses(Long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grup bulunamadı: " + groupId));

        return expenseRepository.findByGroup(group).stream()
                .map(exp -> new ExpenseDto(
                        exp.getId(),
                        exp.getTitle(),
                        exp.getAmount(),
                        exp.getCreatedBy().getUserCode(),
                        exp.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<BalanceDto> getGroupBalances(Long groupId, User currentUser) {
        // 1) Grup kontrolü
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grup bulunamadı: " + groupId));

        // 2) Üyelik kontrolü (yalnızca üye görüntüleyebilir)
        boolean isMember = membershipRepository
                .findByGroupAndUser(group, currentUser)
                .filter(Membership::isAccepted)
                .isPresent();
        if (!isMember) {
            throw new SecurityException("Bu grubun üyesi değilsiniz");
        }

        // 3) Tüm harcamaları al
        List<Expense> expenses = expenseRepository.findByGroup(group);

        // 4) Borç matrisi için geçici bir map oluştur
        //    key = debtorCode + "->" + creditorCode, value = toplam tutar
        Map<String, BigDecimal> netMap = new HashMap<>();

        for (Expense exp : expenses) {
            String creditor = exp.getCreatedBy().getUserCode();
            // split’leri oku
            for (ExpenseSplit split : exp.getSplits()) {
                String debtor = split.getUser().getUserCode();
                // Kişi kendi harcamasını ödemez
                if (debtor.equals(creditor)) continue;
                String key = debtor + "->" + creditor;
                netMap.merge(key, split.getAmountOwed(), BigDecimal::add);
            }
        }

        // 5) Map’i List<BalanceDto>’ye dönüştür
        return netMap.entrySet().stream()
                .map(e -> {
                    String[] parts = e.getKey().split("->");
                    return new BalanceDto(parts[0], parts[1], e.getValue());
                })
                .collect(Collectors.toList());
    }
}
