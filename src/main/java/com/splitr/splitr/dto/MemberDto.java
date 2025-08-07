package com.splitr.splitr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class MemberDto {
        private String userCode;
        private String email;
        private String firstName;
        private String lastName;
}
