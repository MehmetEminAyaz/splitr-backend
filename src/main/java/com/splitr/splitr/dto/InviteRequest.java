package com.splitr.splitr.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InviteRequest {

    @NotBlank(message = "Davet edilecek kullanıcının kodunu giriniz.")
    private String userCode;
}
