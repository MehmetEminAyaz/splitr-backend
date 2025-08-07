package com.splitr.splitr.repository;

import com.splitr.splitr.entity.Group;
import com.splitr.splitr.entity.Membership;
import com.splitr.splitr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership,Long> {
    List<Membership> findByUserAndIsAcceptedTrue(User user);
    Optional<Membership> findByGroupAndUser(Group group,User user);
    List<Membership> findByGroupAndIsAcceptedTrue(Group group);
    List<Membership> findByUserAndIsAcceptedFalse(User user);
}
