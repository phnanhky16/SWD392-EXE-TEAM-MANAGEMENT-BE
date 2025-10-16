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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class JoinServiceImpl implements JoinService {

    GroupRepository groupRepository;
    UserRepository userRepository;
    JoinRepository joinRepository;
    GroupMemberRepository groupMemberRepository;
    PostRepository postRepository;
    VoteService voteService;
    NotificationRepository notificationRepository;
    SimpMessagingTemplate messagingTemplate;

    @Override
    public Void joinGroup(Long groupId) {
        User user = getCurrentUser();
        if (groupMemberRepository.existsByUser(user)) {
            throw new AppException(ErrorCode.USER_ALREADY_IN_GROUP);
        }
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
        if (group.getStatus() == GroupStatus.FORMING) {
            group.setStatus(GroupStatus.ACTIVE);
            groupMemberRepository.save(GroupMember.builder()
                    .group(group)
                    .user(user)
                    .membershipRole(MembershipRole.LEADER)
                    .build());
            joinRepository.save(Join.builder()
                    .toGroup(group)
                    .fromUser(user)
                    .status(JoinStatus.ACCEPTED)
                    .build());
            postRepository.deletePostByUser(user);
            groupRepository.save(group);
            sendNotification(user, "🎉 Bạn đã tạo nhóm thành công!", NotificationType.SYSTEM);
            messagingTemplate.convertAndSend("/topic/groups",
                    "Group " + group.getTitle() + " đã được tạo bởi " + user.getFullName());
            return null;
        }
        if (group.getStatus() == GroupStatus.ACTIVE) {
            groupMemberRepository.save(GroupMember.builder()
                    .group(group)
                    .user(user)
                    .membershipRole(MembershipRole.MEMBER)
                    .build());
            joinRepository.save(Join.builder()
                    .toGroup(group)
                    .fromUser(user)
                    .status(JoinStatus.ACCEPTED)
                    .build());
            postRepository.deletePostByUser(user);
            List<User> members = groupMemberRepository.findUsersByGroup(group);
            for (User member : members) {
                if (!member.getId().equals(user.getId())) {
                    sendNotification(member,
                            "👋 Thành viên mới " + user.getFullName() + " vừa tham gia nhóm " + group.getTitle(),
                            NotificationType.JOIN_ACCEPTED);
                }
            }
            sendNotification(user,
                    "🎉 Bạn đã tham gia thành công nhóm " + group.getTitle(),
                    NotificationType.SYSTEM);
            messagingTemplate.convertAndSend("/topic/group/" + groupId,
                    "User " + user.getFullName() + " joined the group");
            return null;
        }
        joinRequest(groupId, user.getId());
        postRepository.deletePostByUser(user);
        return null;
    }

    @Override
    public Void joinRequest(Long groupId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
        if (joinRepository.existsByFromUserAndToGroup(user, group)) {
            throw new AppException(ErrorCode.DUPLICATE_JOIN_REQUEST);
        }
        joinRepository.save(Join.builder()
                .toGroup(group)
                .fromUser(user)
                .status(JoinStatus.PENDING)
                .build());
        List<User> members = groupMemberRepository.findUsersByGroup(group);
        for (User member : members) {
            sendNotification(member,
                    "📨 " + user.getFullName() + " đã gửi yêu cầu tham gia nhóm " + group.getTitle(),
                    NotificationType.JOIN_REQUEST);
        }
        sendNotification(user,
                "✅ Yêu cầu tham gia nhóm " + group.getTitle() + " đã được gửi.",
                NotificationType.SYSTEM);
        voteService.voteJoin(groupId, userId);
        messagingTemplate.convertAndSend("/topic/group/" + groupId,
                "📢 " + user.getFullName() + " has requested to join the group.");
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

    private void sendNotification(User user, String content, NotificationType type) {
        notificationRepository.save(Notification.builder()
                .receiver(user)
                .content(content)
                .type(type)
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build());
    }
}
