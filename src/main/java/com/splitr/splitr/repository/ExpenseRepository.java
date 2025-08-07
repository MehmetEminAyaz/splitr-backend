package com.splitr.splitr.repository;

import com.splitr.splitr.entity.Expense;
import com.splitr.splitr.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense,Long> {
    List<Expense> findByGroup(Group group);
    
    @Query("SELECT COALESCE(SUM(es.amountOwed), 0) FROM ExpenseSplit es WHERE es.user.userCode = :userCode")
    BigDecimal sumOwedAmountByUser(@Param("userCode") String userCode);
    
    @Query("SELECT COALESCE(SUM(es.amountOwed), 0) FROM ExpenseSplit es WHERE es.expense.createdBy.userCode = :userCode")
    BigDecimal sumOwedToUser(@Param("userCode") String userCode);
}
