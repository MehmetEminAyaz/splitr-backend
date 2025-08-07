package com.splitr.splitr.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @Email(message = "Geçerli bir mail adresi giriniz.")
    @NotBlank(message = "Email alanı boş olamaz.")
    private String email;
    @NotBlank(message = "Şifre boş olamaz.")
    private String password;
}
