package com.swd.exe.teammanagement.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.swd.exe.teammanagement.dto.request.GroupCreateRequest;
import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.dto.response.PagingResponse;
import com.swd.exe.teammanagement.dto.response.UserResponse;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;

public interface GroupService {
    GroupResponse getGroupById(Long groupId);
    List<GroupResponse> getAllGroups();
    UserResponse getGroupLeader(Long groupId);
    String changeGroupType();
    GroupResponse getGroupByUserId(Long userId);
    String leaveGroup();
    List<GroupResponse> getAvailableGroups();
    String doneTeam();
    String createGroup(int size,long semesterId);
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
    String removeMemberByLeader(Long userId);
    GroupResponse updateGroupInfo(GroupCreateRequest request);
    String changeLeader(Long newLeaderId);
    List<GroupResponse> getGroupsBySemester(Long semesterId);
    GroupResponse activateGroup(Long groupId);
    GroupResponse deactivateGroup(Long groupId);
    GroupResponse changeGroupActiveStatus(Long groupId);
    Page<GroupResponse> getMyAssignedGroups(int page, int size, boolean includeHistory);
}
