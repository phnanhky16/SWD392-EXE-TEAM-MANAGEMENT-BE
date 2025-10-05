package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.entity.User;

import java.util.List;

public interface GroupMemberService {
    List<User> getMembersByGroupId(Long groupId);
    int getGroupMemberCount(Long groupId);
    List<Major> getMajorDistribution(Long groupId);
    GroupResponse getGroupInfo();
    void removeMemberByLeader(Long userId, Long groupId, Long leaderId);
}
