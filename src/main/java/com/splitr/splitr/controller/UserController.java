package com.splitr.splitr.controller;

import com.splitr.splitr.dto.UpdateUserRequest;
import com.splitr.splitr.dto.UserBalanceDto;
import com.splitr.splitr.dto.UserDto;
import com.splitr.splitr.entity.User;
import com.splitr.splitr.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal User user){
        return  userService.getCurrentUser(user);
    }
    @PutMapping("/me")
    public UserDto updateMe(
            @RequestBody @Valid UpdateUserRequest req,
            @AuthenticationPrincipal User currentUser
    ) {
        return userService.updateCurrentUser(req, currentUser);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe(@AuthenticationPrincipal User currentUser) {
        userService.deleteCurrentUser(currentUser);
    }
    @GetMapping("/me/balances")
    public ResponseEntity<UserBalanceDto> getMyBalanceSummary(@AuthenticationPrincipal User currentUser) {
        UserBalanceDto dto = userService.getCurrentUserBalanceSummary(currentUser.getEmail());
        return ResponseEntity.ok(dto);
    }
}
