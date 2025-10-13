package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.entity.*;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.idea_join_post_score.JoinStatus;
import com.swd.exe.teammanagement.enums.notification.NotificationStatus;
import com.swd.exe.teammanagement.enums.notification.NotificationType;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.*;
import com.swd.exe.teammanagement.service.JoinService;
import com.swd.exe.teammanagement.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class JoinServiceImpl implements JoinService
{
    GroupRepository groupRepository;
    UserRepository userRepository;
    JoinRepository joinRepository;
    GroupMemberRepository groupMemberRepository;
    VoteService voteService;
    NotificationRepository notificationRepository;
    PostRepository postRepository;

    @Override
    public Void joinGroup(Long groupId) {
        User user = getCurrentUser();
        if(groupMemberRepository.existsByUser(user)){
            throw new AppException(ErrorCode.USER_ALREADY_IN_GROUP);
        }
        Group g = groupRepository.findById(groupId).orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
        if(g.getStatus() == GroupStatus.FORMING) {
            g.setStatus(GroupStatus.ACTIVE);
            groupMemberRepository.save(GroupMember.builder()
                    .group(g)
                    .user(user)
                    .membershipRole(MembershipRole.LEADER)
                    .build());
            groupRepository.save(g);
            joinRepository.save(Join.builder().toGroup(g).fromUser(user).status(JoinStatus.ACCEPTED).build());
            postRepository.deletePostByUser(user);
        }else if (g.getStatus() == GroupStatus.ACTIVE) {
            groupMemberRepository.save(GroupMember.builder()
                    .group(g)
                    .user(user)
                    .membershipRole(MembershipRole.MEMBER)
                    .build());
            joinRepository.save(Join.builder().toGroup(g).fromUser(user).status(JoinStatus.ACCEPTED).build());
            postRepository.deletePostByUser(user);
        }else {
            joinRequest(groupId,user.getId());
            postRepository.deletePostByUser(user);
        }
        return null;
    }

    @Override
    public Void joinRequest(Long groupId,Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
        Group g = groupRepository.findById(groupId).orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
        List<User> users = groupMemberRepository.findUsersByGroup(g);
        joinRepository.save(Join.builder().toGroup(g).fromUser(user).status(JoinStatus.PENDING).build());
        for (User u : users) {
            notificationRepository.save(Notification.builder().content("User "+ user.getEmail()+" wants to join your group").type(NotificationType.JOIN_REQUEST).status(NotificationStatus.UNREAD).user(u).createdAt(LocalDateTime.now()).build());
        }
        voteService.voteJoin(groupId,userId);
        return null;
    }

    @Override
    public List<Join> getPendingJoinRequests(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
        return joinRepository.findByToGroupAndStatus(group, JoinStatus.PENDING);
    }

    @Override
    public List<Join> getMyJoinRequests() {
        User user = getCurrentUser();
        return joinRepository.findByFromUserAndStatus(user, JoinStatus.PENDING);
    }

    @Override
    public Void cancelJoinRequest(Long joinId) {
        Join join = joinRepository.findById(joinId)
                .orElseThrow(() -> new AppException(ErrorCode.JOIN_REQUEST_NOT_FOUND));
        User user = getCurrentUser();
        
        if (!join.getFromUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        
        if (join.getStatus() != JoinStatus.PENDING) {
            throw new AppException(ErrorCode.JOIN_REQUEST_ALREADY_PROCESSED);
        }
        
        joinRepository.delete(join);
        return null;
    }

    private User getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
    }
}
