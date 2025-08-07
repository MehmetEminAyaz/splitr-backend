package com.splitr.splitr.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.NotFound;

@Data
public class RegisterRequest {

    @NotBlank(message = "İsim alanı boş olamaz.")
    private String firstName;

    @NotBlank(message = "Soyad alanı boş olamaz.")
    private String lastName;

    @NotBlank(message = "Email alanı boş olamaz.")
    @Email(message = "Geçerli bir email adresi giriniz.")
    private String email;

    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 6,max=20,message = "Şifre 6-20 karakter arasında olmalıdır.")
    private String password;


}
