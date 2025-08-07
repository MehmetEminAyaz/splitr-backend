package com.splitr.splitr.service;

import com.splitr.splitr.dto.CreateGroupRequest;
import com.splitr.splitr.dto.GroupDto;
import com.splitr.splitr.dto.MemberDto;
import com.splitr.splitr.dto.UpdateGroupRequest;
import com.splitr.splitr.entity.Group;
import com.splitr.splitr.entity.Membership;
import com.splitr.splitr.entity.User;
import com.splitr.splitr.repository.GroupRepository;
import com.splitr.splitr.repository.MembershipRepository;
import com.splitr.splitr.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService{
    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    @Override
    public GroupDto createGroup(CreateGroupRequest request, User currentUser) {
        Group group=Group.builder()
                .name(request.getName())
                .createdBy(currentUser)
                .build();
        group=groupRepository.save(group);

        Membership membership=Membership.builder()
                .user(currentUser)
                .group(group)
                .isAccepted(true)
                .joinedAt(LocalDateTime.now())
                .build();
        membershipRepository.save(membership);

        return new GroupDto(group.getId(),group.getName(),currentUser.getUserCode());

    }

    @Override
    public List<GroupDto> getUserGroups(User user) {
        List<Membership> memberships=membershipRepository.findByUserAndIsAcceptedTrue(user);

        return memberships.stream()
                .map(m -> new GroupDto(
                        m.getGroup().getId(),
                        m.getGroup().getName(),
                        m.getGroup().getCreatedBy().getUserCode()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void inviteMember(Long groupId, String userCode, User user) {
        // (1) Grup var mı kontrolü
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grup bulunamadı: " + groupId));

        // (2) Davet edilecek kullanıcı var mı kontrolü
        User invitee = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı: " + userCode));

        // (3) Zaten davetli/üye mi kontrol et
        membershipRepository.findByGroupAndUser(group, invitee)
                .ifPresent(m -> {
                    throw new IllegalStateException("Zaten davet edilmiş veya gruptasınız");
                });

        // (4) Yeni davet kaydı (isAccepted = false)
        Membership membership = Membership.builder()
                .group(group)
                .user(invitee)
                .isAccepted(false)
                .joinedAt(LocalDateTime.now())
                .build();
        membershipRepository.save(membership);
    }

    @Override
    public void acceptInvitation(Long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grup bulunamadı: " + groupId));
        Membership membership = membershipRepository
                .findByGroupAndUser(group, currentUser)
                .orElseThrow(() -> new IllegalArgumentException("Davetiye bulunamadı"));
        if (membership.isAccepted()) {
            throw new IllegalStateException("Zaten gruba üye olmuşsunuz");
        }
        membership.setAccepted(true);
        membershipRepository.save(membership);
    }

    @Override
    public List<MemberDto> getGroupMembers(Long groupId, User currentUser) {
        // (1) Grup var mı?
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grup bulunamadı: " + groupId));

        // (2) (İsteğe bağlı) Sadece gruptaki kullanıcılar görebilsin:
        boolean isMember = membershipRepository
                .findByGroupAndUser(group, currentUser)
                .filter(Membership::isAccepted)
                .isPresent();
        if (!isMember) {
            throw new SecurityException("Bu grubun üyesi değilsiniz");
        }

        // (3) Kabul edilmiş üyeleri al ve DTO’ya dönüştür
        List<Membership> accepted = membershipRepository
                .findByGroupAndIsAcceptedTrue(group);

        return accepted.stream()
                .map(m -> {
                    User u = m.getUser();
                    return new MemberDto(
                            u.getUserCode(),
                            u.getEmail(),
                            u.getFirstName(),
                            u.getLastName()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GroupDto updateGroup(Long groupId,
                                UpdateGroupRequest req,
                                User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grup bulunamadı: " + groupId));
        // Owner kontrolü kaldırıldı - herhangi bir üye grup güncelleyebilir
        group.setName(req.getName());
        Group updated = groupRepository.save(group);
        return new GroupDto(
                updated.getId(),
                updated.getName(),
                updated.getCreatedBy().getUserCode()
        );
    }

    @Override
    @Transactional
    public void deleteGroup(Long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grup bulunamadı: " + groupId));
        // Owner kontrolü kaldırıldı - herhangi bir üye grup silebilir
        groupRepository.delete(group);
    }

    @Override
    @Transactional
    public void removeMember(Long groupId,
                             String userCode,
                             User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grup bulunamadı: " + groupId));
        // Opsiyonel: sadece sahibi veya kendisi çıkarabilir
        // if (!group.getCreatedBy().equals(currentUser) && !currentUser.getUserCode().equals(userCode)) ...
        User target = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı: " + userCode));
        Membership m = membershipRepository
                .findByGroupAndUser(group, target)
                .orElseThrow(() -> new IllegalArgumentException("Üyelik bulunamadı"));
        membershipRepository.delete(m);
    }
}

