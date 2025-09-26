package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.request.UserUpdateRequest;
import com.swd.exe.teammanagement.dto.response.UserResponse;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.mapper.UserMapper;
import com.swd.exe.teammanagement.repository.MajorRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    MajorRepository majorRepository;

    @Override
    public UserResponse getUserById(Long id) {
        return userMapper.toUserResponse(
                userRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED))
        );
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userMapper.toUserResponseList(userRepository.findAll());
    }

    @Override
    public UserResponse updateUser(Long id,UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
        Major major = majorRepository.findById(request.getCode())
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_UNEXISTED));
        user.setMajor(major);
        userMapper.toUserUpdate(user, request);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByStudentCode(name).orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateMyInfo(UserUpdateRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByStudentCode(name).orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
        userMapper.toUserUpdate(user, request);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse changeStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
        user.setIsActive(!user.getIsActive());
        return userMapper.toUserResponse(userRepository.save(user));
    }
}
