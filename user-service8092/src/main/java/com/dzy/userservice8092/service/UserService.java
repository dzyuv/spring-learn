package com.dzy.userservice8092.service;


import com.dzy.common.entity.User;
import com.dzy.userservice8092.dto.ChangePasswordRequest;
import com.dzy.userservice8092.dto.LoginRequest;
import com.dzy.userservice8092.dto.RegisterRequest;
import com.dzy.userservice8092.dto.TokenPair;

public interface UserService {

    TokenPair login(LoginRequest request);

    User register(RegisterRequest request);

    User getCurrentUser(Long userId);

    void changePassword(Long userId, ChangePasswordRequest request);
}
