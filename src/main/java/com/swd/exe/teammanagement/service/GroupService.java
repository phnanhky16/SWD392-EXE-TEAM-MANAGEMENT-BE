package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.request.GroupCreateRequest;
import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.dto.response.PagingResponse;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;

import java.util.List;

public interface GroupService {
    GroupResponse getGroupById(Long groupId);
    List<GroupResponse> getAllGroups();

    Void changeGroupType();
    GroupResponse getGroupByUserId(Long userId);
    Void leaveGroup();
    List<GroupResponse> getAvailableGroups();
    Void doneTeam();
    Void createGroup(int size);
    PagingResponse<GroupResponse> searchGroups(
            String q,
            GroupStatus status,
            GroupType type,
            int page,
            int size,
            String sort,
            String dir
    );
    List<GroupResponse> getCurrentGroupList();
    List<User> getMembersByGroupId(Long groupId);
    int getGroupMemberCount(Long groupId);
    List<Major> getMajorDistribution(Long groupId);
    GroupResponse getMyGroup();
    Void removeMemberByLeader(Long userId);
    GroupResponse updateGroupInfo(GroupCreateRequest request);
}
