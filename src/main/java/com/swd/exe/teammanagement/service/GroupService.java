package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.dto.response.PagingResponse;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;

import java.util.List;

public interface GroupService {
    GroupResponse getGroupById(Long groupId);
    Void deleteGroup();
    GroupResponse changeGroupType();
    GroupResponse getGroup(Long userId);
    Void leaveGroup();
    List<GroupResponse> getAvailableGroups();
    Void doneTeam();
    Void createGroup(int size);
    PagingResponse<GroupResponse> searchGroups(
            String q, GroupStatus status, GroupType type,
            int page, int size, String sort, String dir
    );
}
