package com.splitr.splitr.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateExpenseRequest {
    @NotBlank(message = "Başlık boş olamaz")
    private String title;

    @NotNull(message = "Tutar girilmeli")
    @DecimalMin(value = "0.01", message = "Tutar 0.01’den büyük olmalı")
    private BigDecimal amount;

    @NotEmpty(message = "En az bir kullanıcı seçilmeli")
    private List<@NotBlank(message = "Kullanıcı kodu boş olamaz") String> memberUserCodes;
}
