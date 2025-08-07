package com.splitr.splitr.repository;

import com.splitr.splitr.entity.ExpenseSplit;
import com.splitr.splitr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit,Long> {
    
    // Kullanıcının belirli bir gruptaki borçlarını getir
    @Query("SELECT es FROM ExpenseSplit es WHERE es.user.userCode = :userCode AND es.expense.group.id = :groupId")
    List<ExpenseSplit> findByUserAndGroup(@Param("userCode") String userCode, @Param("groupId") Long groupId);
    
    // Kullanıcının belirli bir gruptaki toplam borcunu getir
    @Query("SELECT COALESCE(SUM(es.amountOwed), 0) FROM ExpenseSplit es WHERE es.user.userCode = :userCode AND es.expense.group.id = :groupId")
    Double getTotalOwedByUserInGroup(@Param("userCode") String userCode, @Param("groupId") Long groupId);
}
