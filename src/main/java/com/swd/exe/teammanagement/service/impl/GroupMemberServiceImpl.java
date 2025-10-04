package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.service.GroupMemberService;

import java.util.List;

public class GroupMemberServiceImpl implements GroupMemberService {
    @Override
    public List<User> getMembersByGroupId(Long groupId) {
        return List.of();
    }

    @Override
    public int getGroupMemberCount(Long groupId) {
        return 0;
    }

    @Override
    public List<Major> getMajorDistribution(Long groupId) {
        return List.of();
    }

    @Override
    public User getGroupLeader(Long groupId) {
        return null;
    }

    @Override
    public GroupResponse getGroupInfo() {
        return null;
    }

    @Override
    public void removeMember(Long userId, Long groupId, Long leaderId) {

    }
}
