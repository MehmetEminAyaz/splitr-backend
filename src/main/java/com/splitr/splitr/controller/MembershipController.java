package com.splitr.splitr.controller;

import com.splitr.splitr.dto.GroupInvitationDto;
import com.splitr.splitr.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.splitr.splitr.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    // Kullanıcının gelen davetlerini getir
    @GetMapping("/invitations")
    public ResponseEntity<List<GroupInvitationDto>> getMyInvitations(@AuthenticationPrincipal User currentUser) {
        List<GroupInvitationDto> invitations = membershipService.getPendingInvitationsForUser(currentUser.getEmail());
        return ResponseEntity.ok(invitations);
    }
}
