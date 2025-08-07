package com.splitr.splitr.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePaymentRequest {
    @NotBlank(message = "Alacaklı kullanıcı kodu boş olamaz")
    private String receiverUserCode;

    @NotNull(message = "Tutar girilmeli")
    @DecimalMin(value = "0.01", message = "Tutar en az 0.01 olmalı")
    private BigDecimal amount;

}
