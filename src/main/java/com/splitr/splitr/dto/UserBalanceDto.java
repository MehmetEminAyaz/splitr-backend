package com.splitr.splitr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBalanceDto {
    private BigDecimal totalOwedToOthers;
    private BigDecimal totalOwedByOthers;
}