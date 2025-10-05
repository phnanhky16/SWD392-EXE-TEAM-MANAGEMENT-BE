package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.request.GroupCreateFirstRequest;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.Join;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.idea_join_post_score.JoinStatus;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.GroupMemberRepository;
import com.swd.exe.teammanagement.repository.GroupRepository;
import com.swd.exe.teammanagement.repository.JoinRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.service.JoinService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class JoinServiceImpl implements JoinService
{
    GroupRepository groupRepository;
    UserRepository userRepository;
    JoinRepository joinRepository;
    GroupMemberRepository groupMemberRepository;
    @Override
    public Void joinGroupFirst(Long groupId, GroupCreateFirstRequest request) {
        User user = getCurrentUser();
        if(groupMemberRepository.existsByUser(user)){
            throw new AppException(ErrorCode.USER_ALREADY_IN_GROUP);
        }
        Group g = groupRepository.findById(groupId).orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
        g.setTitle(request.getTitle());
        g.setDescription(request.getDescription());
        g.setLeader(user);
        g.setStatus(GroupStatus.ACTIVE);
        groupMemberRepository.save(GroupMember.builder()
                .group(g)
                .user(user)
                .role(MembershipRole.LEADER)
                .build());
        groupRepository.save(g);
        joinRepository.save(Join.builder().toGroup(g).fromUser(user).status(JoinStatus.ACCEPTED).build());
        return null;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
    }
}
