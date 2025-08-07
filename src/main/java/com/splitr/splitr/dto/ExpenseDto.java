package com.splitr.splitr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ExpenseDto {
    private Long id;
    private String title;
    private BigDecimal amount;
    private String createdByUserCode;
    private LocalDateTime createdAt;
}
