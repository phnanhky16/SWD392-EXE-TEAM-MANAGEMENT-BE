package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.request.GroupCreateRequest;
import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.dto.response.PagingResponse;
import com.swd.exe.teammanagement.dto.response.UserResponse;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;

import java.util.List;
import java.util.Set;

public interface GroupService {
    GroupResponse getGroupById(Long groupId);
    List<GroupResponse> getAllGroups();
    UserResponse getGroupLeader(Long groupId);
    Void changeGroupType();
    GroupResponse getGroupByUserId(Long userId);
    Void leaveGroup();
    List<GroupResponse> getAvailableGroups();
    Void doneTeam();
    Void createGroup(int size,long semesterId);
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
    List<UserResponse> getMembersByGroupId(Long groupId);
    int getGroupMemberCount(Long groupId);
    Set<Major> getMajorDistribution(Long groupId);
    GroupResponse getMyGroup();
    Void removeMemberByLeader(Long userId);
    GroupResponse updateGroupInfo(GroupCreateRequest request);
    Void changeLeader(Long newLeaderId);
    List<GroupResponse> getGroupsBySemester(Long semesterId);
}
