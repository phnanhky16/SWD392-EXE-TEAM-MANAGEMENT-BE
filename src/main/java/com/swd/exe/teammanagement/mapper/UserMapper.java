package com.swd.exe.teammanagement.mapper;

import com.swd.exe.teammanagement.dto.request.UserUpdateRequest;
import com.swd.exe.teammanagement.dto.response.UserResponse;
import com.swd.exe.teammanagement.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);
    void toUserUpdate(@MappingTarget User user, UserUpdateRequest userUpdateRequest);

    List<UserResponse> toUserResponseList(List<User> users);
}
