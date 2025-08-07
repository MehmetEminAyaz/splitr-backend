package com.splitr.splitr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private String payerUserCode;
    private String receiverUserCode;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
}
