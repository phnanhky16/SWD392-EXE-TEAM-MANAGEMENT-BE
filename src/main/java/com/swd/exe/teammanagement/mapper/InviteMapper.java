package com.swd.exe.teammanagement.mapper;

import com.swd.exe.teammanagement.dto.response.InviteResponse;
import com.swd.exe.teammanagement.entity.GroupInvite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, GroupMapper.class})
public interface InviteMapper {
    @Mapping(target = "group", source = "group")
    @Mapping(target = "inviter", source = "inviter")
    @Mapping(target = "invitee", source = "invitee")
    InviteResponse toInviteResponse(GroupInvite entity);
}
