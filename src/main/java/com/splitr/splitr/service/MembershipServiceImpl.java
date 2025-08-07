package com.splitr.splitr.service;

import com.splitr.splitr.dto.GroupInvitationDto;
import com.splitr.splitr.entity.Membership;
import com.splitr.splitr.entity.User;
import com.splitr.splitr.repository.MembershipRepository;
import com.splitr.splitr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService{
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    @Override
    public List<GroupInvitationDto> getPendingInvitationsForUser(String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı"));

        List<Membership> pendingInvites = membershipRepository.findByUserAndIsAcceptedFalse(user);

        return pendingInvites.stream().map(m -> {
            return new GroupInvitationDto(
                    m.getId(),
                    m.getGroup().getId(),
                    m.getGroup().getName()

            );
        }).collect(Collectors.toList());
    }
}
