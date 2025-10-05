package com.swd.exe.teammanagement.mapper;

import com.swd.exe.teammanagement.dto.request.UserUpdateRequest;
import com.swd.exe.teammanagement.dto.response.UserResponse;
import com.swd.exe.teammanagement.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "majorCode", expression = "java(mapMajorCode(user.getMajor()))")
    @Mapping(target = "majorName", expression = "java(mapMajorName(user.getMajor()))")
    UserResponse toUserResponse(User user);

    default String mapMajorCode(com.swd.exe.teammanagement.entity.Major m) {
        return m == null ? null : m.getCode();
    }
    default String mapMajorName(com.swd.exe.teammanagement.entity.Major m) {
        return m == null ? null : m.getName();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUserUpdate(@MappingTarget User user, UserUpdateRequest req);

    List<UserResponse> toUserResponseList(List<User> users);
}

