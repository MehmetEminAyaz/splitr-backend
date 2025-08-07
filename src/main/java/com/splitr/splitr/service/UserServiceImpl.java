package com.splitr.splitr.service;

import com.splitr.splitr.dto.UpdateUserRequest;
import com.splitr.splitr.dto.UserBalanceDto;
import com.splitr.splitr.dto.UserDto;
import com.splitr.splitr.entity.User;
import com.splitr.splitr.repository.ExpenseRepository;
import com.splitr.splitr.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    public UserDto getCurrentUser(User user) {
        return new UserDto(user.getEmail(), user.getFirstName(), user.getLastName(), user.getUserCode());
    }

    @Override
    @Transactional
    public UserDto updateCurrentUser(UpdateUserRequest req, User currentUser) {
        // Email benzersiz mi kontrol et
        if (!currentUser.getEmail().equals(req.getEmail())
                && userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email zaten kayıtlı");
        }
        currentUser.setFirstName(req.getFirstName());
        currentUser.setLastName(req.getLastName());
        currentUser.setEmail(req.getEmail());
        User updated = userRepository.save(currentUser);
        return new UserDto(
                updated.getEmail(),
                updated.getFirstName(),
                updated.getLastName(),
                updated.getUserCode()
        );
    }

    @Override
    @Transactional
    public void deleteCurrentUser(User currentUser) {
        // Tüm ilişkiler cascade ayarınızın durumuna göre silinebilir
        userRepository.delete(currentUser);
    }

    @Override
    public UserBalanceDto getCurrentUserBalanceSummary(String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı"));

        // Kullanıcının başkalarına olan toplam borcu
        BigDecimal owedToOthers = expenseRepository.sumOwedAmountByUser(user.getUserCode());
        if (owedToOthers == null) owedToOthers = BigDecimal.ZERO;

        // Başkalarının bu kullanıcıya olan toplam borcu
        BigDecimal owedByOthers = expenseRepository.sumOwedToUser(user.getUserCode());
        if (owedByOthers == null) owedByOthers = BigDecimal.ZERO;

        return new UserBalanceDto(owedToOthers, owedByOthers);
    }
}
