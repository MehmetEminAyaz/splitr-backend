package com.splitr.splitr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupDto {
    private Long id;
    private String name;
    private String ownerUserCode;
}
