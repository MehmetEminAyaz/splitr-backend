package com.splitr.splitr.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateGroupRequest {
    @NotBlank(message = "Grup adı boş olamaz")
    private String name;
}
