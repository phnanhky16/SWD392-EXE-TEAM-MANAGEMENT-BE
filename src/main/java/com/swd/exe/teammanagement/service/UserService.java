package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.request.UserUpdateRequest;
import com.swd.exe.teammanagement.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserUpdateRequest request);
    UserResponse getMyInfo();
    UserResponse updateMyInfo(UserUpdateRequest request);
    UserResponse changeStatus(Long id);
    UserResponse updateRole(Long id);
}
