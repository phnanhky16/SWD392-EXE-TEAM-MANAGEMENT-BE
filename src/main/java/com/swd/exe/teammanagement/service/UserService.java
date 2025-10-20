package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.request.UserUpdateRequest;
import com.swd.exe.teammanagement.dto.response.PagingResponse;
import com.swd.exe.teammanagement.dto.response.UserResponse;
import com.swd.exe.teammanagement.enums.user.UserRole;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserUpdateRequest request);
    UserResponse getMyInfo();
    UserResponse updateMyInfo(UserUpdateRequest request);
    UserResponse changeStatus(Long id);
    UserResponse updateRole(Long id);
    PagingResponse<UserResponse> searchUsers(
            String q, UserRole role, Boolean active, String majorCode,
            int page, int size, String sort, String dir
    );
    UserResponse uploadAvatar(Long userId, MultipartFile avatar) throws IOException;
    UserResponse uploadCV(Long userId, MultipartFile cvFile) throws IOException;
}
