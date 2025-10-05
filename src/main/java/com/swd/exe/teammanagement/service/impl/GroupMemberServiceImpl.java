package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.GroupMemberRepository;
import com.swd.exe.teammanagement.repository.GroupRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class GroupMemberServiceImpl implements GroupMemberService {
    GroupRepository groupRepository;
    UserRepository userRepository;
    GroupMemberRepository groupMemberRepository;
    @Override
    public List<User> getMembersByGroupId(Long groupId) {
        return groupMemberRepository.findUsersByGroupId(groupId);
    }

    @Override
    public int getGroupMemberCount(Long groupId) {
        return groupMemberRepository.countByGroupId(groupId);
    }

    @Override
    public List<Major> getMajorDistribution(Long groupId) {
        Set<Major> majors = groupMemberRepository.findMajorsByGroupId(groupId);
        return List.copyOf(majors);
    }

    @Override
    public GroupResponse getGroupInfo() {
        User user = getCurrentUser();

        return null;
    }

    @Override
    public void removeMemberByLeader(Long userId, Long groupId, Long leaderId) {

    }
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
    }

}
