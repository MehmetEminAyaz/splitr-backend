package com.splitr.splitr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceDto {
    private String fromUserCode;
    private String toUserCode;
    private BigDecimal amount;
}
