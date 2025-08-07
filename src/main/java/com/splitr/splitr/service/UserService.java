package com.splitr.splitr.service;


import com.splitr.splitr.dto.UpdateUserRequest;
import com.splitr.splitr.dto.UserBalanceDto;
import com.splitr.splitr.dto.UserDto;
import com.splitr.splitr.entity.User;


public interface UserService {
    UserDto getCurrentUser(User user);
    UserDto updateCurrentUser(UpdateUserRequest req, User currentUser);
    void deleteCurrentUser(User currentUser);
    UserBalanceDto getCurrentUserBalanceSummary(String currentUserEmail);
}
