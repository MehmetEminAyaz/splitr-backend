package com.splitr.splitr.repository;

import com.splitr.splitr.entity.Group;
import com.splitr.splitr.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findByGroup(Group group);
}
