package com.splitr.splitr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupInvitationDto {
    private Long invitationId;
    private Long groupId;
    private String groupName;

}