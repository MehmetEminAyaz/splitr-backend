package com.splitr.splitr.service;

import com.splitr.splitr.dto.GroupInvitationDto;

import java.util.List;

public interface MembershipService {
    List<GroupInvitationDto> getPendingInvitationsForUser(String currentUserEmail);

}
