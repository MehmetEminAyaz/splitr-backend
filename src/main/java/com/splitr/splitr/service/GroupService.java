package com.splitr.splitr.service;

import com.splitr.splitr.dto.CreateGroupRequest;
import com.splitr.splitr.dto.GroupDto;
import com.splitr.splitr.dto.MemberDto;
import com.splitr.splitr.dto.UpdateGroupRequest;
import com.splitr.splitr.entity.User;

import java.util.List;

public interface GroupService {
    GroupDto createGroup(CreateGroupRequest request, User currentUser);
    List<GroupDto> getUserGroups(User user);
    void inviteMember(Long groupId,String userCode,User user);
    void acceptInvitation(Long groupId,User currentUser);
    List<MemberDto> getGroupMembers(Long groupId,User currentUser);
    GroupDto updateGroup(Long groupId, UpdateGroupRequest req, User currentUser);
    void deleteGroup(Long groupId, User currentUser);
    void removeMember(Long groupId, String userCode, User currentUser);
}
