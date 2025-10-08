package com.swd.exe.teammanagement.mapper;

import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.entity.Group;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupMapper {
    GroupResponse toGroupResponse(Group entity);
}

