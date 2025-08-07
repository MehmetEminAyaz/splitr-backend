package com.splitr.splitr.service;

import com.splitr.splitr.dto.BalanceDto;
import com.splitr.splitr.dto.CreateExpenseRequest;
import com.splitr.splitr.dto.ExpenseDto;
import com.splitr.splitr.entity.User;

import java.util.List;

public interface ExpenseService {
    ExpenseDto createExpense(Long groupId, CreateExpenseRequest req, User currentUser);
    List<ExpenseDto> listExpenses(Long groupId, User currentUser);
    List<BalanceDto> getGroupBalances(Long groupId, User currentUser);
}
