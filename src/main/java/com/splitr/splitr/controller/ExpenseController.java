package com.splitr.splitr.controller;

import com.splitr.splitr.dto.BalanceDto;
import com.splitr.splitr.dto.CreateExpenseRequest;
import com.splitr.splitr.dto.ExpenseDto;
import com.splitr.splitr.entity.User;
import com.splitr.splitr.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ExpenseDto create(
            @PathVariable Long groupId,
            @RequestBody @Valid CreateExpenseRequest req,
            @AuthenticationPrincipal User currentUser
    ) {
        return expenseService.createExpense(groupId, req, currentUser);
    }

    @GetMapping
    public List<ExpenseDto> list(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User currentUser
    ) {
        return expenseService.listExpenses(groupId, currentUser);
    }
    // com/splitr/controller/ExpenseController.java
    @GetMapping("/balances")
    public List<BalanceDto> balances(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User currentUser
    ) {
        return expenseService.getGroupBalances(groupId, currentUser);
    }
}
