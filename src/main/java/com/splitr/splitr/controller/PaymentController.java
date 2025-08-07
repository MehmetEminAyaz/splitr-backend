package com.splitr.splitr.controller;

import com.splitr.splitr.dto.CreatePaymentRequest;
import com.splitr.splitr.dto.PaymentDto;
import com.splitr.splitr.entity.User;
import com.splitr.splitr.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    // Borç ödeme kaydı
    @PostMapping
    public PaymentDto recordPayment(
            @PathVariable Long groupId,
            @RequestBody @Valid CreatePaymentRequest req,
            @AuthenticationPrincipal User currentUser
    ) {
        return paymentService.recordPayment(groupId, req, currentUser);
    }

    // Ödeme geçmişi
    @GetMapping
    public List<PaymentDto> listPayments(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User currentUser
    ) {
        return paymentService.listPayments(groupId, currentUser);
    }
}
