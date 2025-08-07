package com.splitr.splitr.controller;

import com.splitr.splitr.dto.*;
import com.splitr.splitr.entity.User;
import com.splitr.splitr.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    public GroupDto createGroup(@RequestBody @Valid CreateGroupRequest request,
                                @AuthenticationPrincipal User user){
        return groupService.createGroup(request,user);
    }

    @GetMapping
    public List<GroupDto> listGroups(@AuthenticationPrincipal User user){
        return groupService.getUserGroups(user);
    }

    @PostMapping("/{groupId}/invite")
    public void inviteMember(
            @PathVariable Long groupId,
            @RequestBody @Valid InviteRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        groupService.inviteMember(groupId, request.getUserCode(), currentUser);
    }

    @PostMapping("/{groupId}/accept")
    public void acceptInvitation(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User currentUser
    ) {
        groupService.acceptInvitation(groupId, currentUser);
    }

    /** Gruptaki tüm kabul edilmiş üyelerin listesi */
    @GetMapping("/{groupId}/members")
    public List<MemberDto> listGroupMembers(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User currentUser
    ) {
        return groupService.getGroupMembers(groupId, currentUser);
    }

    @PutMapping("/{groupId}")
    public GroupDto renameGroup(
            @PathVariable Long groupId,
            @RequestBody @Valid UpdateGroupRequest req,
            @AuthenticationPrincipal User currentUser
    ) {
        return groupService.updateGroup(groupId, req, currentUser);
    }

    @DeleteMapping("/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User currentUser
    ) {
        groupService.deleteGroup(groupId, currentUser);
    }

    @DeleteMapping("/{groupId}/members/{userCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(
            @PathVariable Long groupId,
            @PathVariable String userCode,
            @AuthenticationPrincipal User currentUser
    ) {
        groupService.removeMember(groupId, userCode, currentUser);
    }
}
