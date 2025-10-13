package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.request.UserUpdateRequest;
import com.swd.exe.teammanagement.dto.response.PagingResponse;
import com.swd.exe.teammanagement.dto.response.UserResponse;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.user.UserRole;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.mapper.UserMapper;
import com.swd.exe.teammanagement.repository.MajorRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.service.UserService;
import com.swd.exe.teammanagement.spec.UserSpecs;
import com.swd.exe.teammanagement.util.PageUtil;
import com.swd.exe.teammanagement.util.SortUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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
        Major major = majorRepository.findById(request.getMajorId())
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_UNEXISTED));
        user.setMajor(major);
        userMapper.toUserUpdate(user, request);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getMyInfo() {
        User user = getCurrentUser();
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateMyInfo(UserUpdateRequest request) {
        User user = getCurrentUser();
        Major major = majorRepository.findById(request.getMajorId())
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_UNEXISTED));
        user.setMajor(major);
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
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
    }

    @Override
    public UserResponse updateRole(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));

        UserRole target = switch (user.getRole()) {
            case LECTURER   -> UserRole.MODERATOR;
            case MODERATOR -> UserRole.LECTURER;
            default -> throw new AppException(ErrorCode.ROLE_UPDATE_NOT_SWITCHABLE);
        };

        user.setRole(target);
        User saved = userRepository.save(user);

        return userMapper.toUserResponse(saved);
    }
    @Override
    @Transactional(readOnly = true)
    public PagingResponse<UserResponse> searchUsers(
            String q, UserRole role, Boolean active, String majorCode,
            int page, int size, String sort, String dir) {

        Sort s = SortUtil.sanitize(sort, dir,
                Set.of("id", "fullName", "email", "studentCode", "createdAt"),
                "id", Sort.Direction.DESC);

        Pageable pageable = SortUtil.pageable1Based(page, size, s);

        Specification<User> spec = Specification.allOf(
                UserSpecs.keyword(q),
                UserSpecs.role(role),
                UserSpecs.active(active)
//                UserSpecs.majorCode(majorCode)
        );

        Page<User> p = userRepository.findAll(spec, pageable);

        return PageUtil.toResponse(p, userMapper::toUserResponse);
    }

}
