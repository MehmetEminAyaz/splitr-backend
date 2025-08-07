package com.splitr.splitr.service;

import com.splitr.splitr.dto.CreatePaymentRequest;
import com.splitr.splitr.dto.PaymentDto;
import com.splitr.splitr.entity.User;

import java.util.List;

public interface PaymentService {
    PaymentDto recordPayment(Long groupId, CreatePaymentRequest req, User currentUser);
    List<PaymentDto> listPayments(Long groupId, User currentUser);
}
