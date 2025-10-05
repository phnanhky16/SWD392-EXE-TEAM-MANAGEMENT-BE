package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.response.GroupResponse;

import java.util.List;

public interface GroupService {
    GroupResponse getGroupById(Long groupId);
    List<GroupResponse> getAllGroups();
    Void deleteGroup();
    GroupResponse changeGroupType();
    GroupResponse getGroup(Long userId);
    Void leaveGroup();
    List<GroupResponse> getAvailableGroups();
    Void doneTeam();
    Void createGroup(int size);
}
