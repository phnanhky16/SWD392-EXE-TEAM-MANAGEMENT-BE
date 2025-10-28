package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.request.InviteRequest;
import com.swd.exe.teammanagement.dto.request.InviteUpdateRequest;
import com.swd.exe.teammanagement.dto.response.InviteResponse;
import com.swd.exe.teammanagement.dto.response.MyInviteResponse;
import com.swd.exe.teammanagement.entity.*;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.invite.InviteStatus;
import com.swd.exe.teammanagement.enums.notification.NotificationStatus;
import com.swd.exe.teammanagement.enums.notification.NotificationType;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.mapper.InviteMapper;
import com.swd.exe.teammanagement.repository.*;
import com.swd.exe.teammanagement.service.InviteService;
import com.swd.exe.teammanagement.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class InviteServiceImpl implements InviteService {

    GroupInviteRepository groupInviteRepository;
    GroupRepository groupRepository;
    UserRepository userRepository;
    GroupMemberRepository groupMemberRepository;
    PostRepository postRepository;
    NotificationRepository notificationRepository;
    InviteMapper inviteMapper;

    @Override
    @Transactional
    public InviteResponse createInvite(InviteRequest request) {
        User inviter = getCurrentUser();
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

        if (!group.isActive()) {
            throw new AppException(ErrorCode.GROUP_NOT_ACTIVE);
        }

        if (group.getStatus() == GroupStatus.LOCKED) {
            throw new AppException(ErrorCode.GROUP_LOCKED);
        }

        boolean isLeader = groupMemberRepository.existsByGroupIdAndUserIdAndMembershipRoleAndActiveTrue(
                group.getId(), inviter.getId(), MembershipRole.LEADER);
        if (!isLeader) {
            throw new AppException(ErrorCode.ONLY_GROUP_LEADER);
        }

        User invitee = userRepository.findById(request.getInviteeId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));

        if (inviter.getId().equals(invitee.getId())) {
            throw new AppException(ErrorCode.CANNOT_INVITE_SELF);
        }

        if (groupMemberRepository.existsByUserAndActiveTrue(invitee)) {
            throw new AppException(ErrorCode.USER_ALREADY_IN_GROUP);
        }

        if (groupInviteRepository.existsByGroupAndInviteeAndStatus(group, invitee, InviteStatus.PENDING)) {
            throw new AppException(ErrorCode.INVITE_ALREADY_EXISTS);
        }

        GroupInvite invite = GroupInvite.builder()
                .group(group)
                .inviter(inviter)
                .invitee(invitee)
                .status(InviteStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        GroupInvite saved = groupInviteRepository.save(invite);

        sendNotification(invitee,
                "📨 Bạn nhận được lời mời tham gia nhóm " + group.getTitle() + " từ " + inviter.getFullName(),
                NotificationType.INVITE);

        return inviteMapper.toInviteResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MyInviteResponse getMyInvites(InviteStatus status, int receivedPage, int receivedSize, int sentPage, int sentSize) {
        User currentUser = getCurrentUser();
        Pageable receivedPageable = buildPageable(receivedPage, receivedSize);
        Pageable sentPageable = buildPageable(sentPage, sentSize);

        Page<GroupInvite> receivedPageData = status == null
                ? groupInviteRepository.findByInvitee(currentUser, receivedPageable)
                : groupInviteRepository.findByInviteeAndStatus(currentUser, status, receivedPageable);

        Page<GroupInvite> sentPageData = status == null
                ? groupInviteRepository.findByInviter(currentUser, sentPageable)
                : groupInviteRepository.findByInviterAndStatus(currentUser, status, sentPageable);

        return MyInviteResponse.builder()
                .received(PageUtil.toResponse(receivedPageData, inviteMapper::toInviteResponse))
                .sent(PageUtil.toResponse(sentPageData, inviteMapper::toInviteResponse))
                .build();
    }

    @Override
    @Transactional
    public InviteResponse respondToInvite(Long inviteId, InviteUpdateRequest request) {
        User currentUser = getCurrentUser();
        GroupInvite invite = groupInviteRepository.findById(inviteId)
                .orElseThrow(() -> new AppException(ErrorCode.INVITE_NOT_FOUND));

        if (!invite.getInvitee().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new AppException(ErrorCode.INVITE_ALREADY_PROCESSED);
        }

        InviteStatus newStatus = request.getStatus();
        if (newStatus == null || newStatus == InviteStatus.PENDING) {
            throw new AppException(ErrorCode.INVITE_INVALID_STATUS);
        }

        if (newStatus == InviteStatus.ACCEPTED) {
            if (groupMemberRepository.existsByUserAndActiveTrue(currentUser)) {
                throw new AppException(ErrorCode.USER_ALREADY_IN_GROUP);
            }

            Group group = invite.getGroup();
            if (group.getStatus() == GroupStatus.LOCKED) {
                throw new AppException(ErrorCode.GROUP_LOCKED);
            }

            List<User> members = groupMemberRepository.findUsersByGroup(group);

            groupMemberRepository.save(GroupMember.builder()
                    .group(group)
                    .user(currentUser)
                    .membershipRole(MembershipRole.MEMBER)
                    .active(true)
                    .build());
            postRepository.deactivatePostsByUser(currentUser);

            invite.setStatus(InviteStatus.ACCEPTED);
            invite.setRespondedAt(LocalDateTime.now());
            GroupInvite savedInvite = groupInviteRepository.save(invite);

            sendNotification(invite.getInviter(),
                    "🎉 " + currentUser.getFullName() + " đã chấp nhận lời mời tham gia nhóm " + group.getTitle(),
                    NotificationType.INVITE_ACCEPTED);

            for (User member : members) {
                sendNotification(member,
                        "👋 Thành viên mới " + currentUser.getFullName() + " vừa tham gia nhóm " + group.getTitle(),
                        NotificationType.JOIN_ACCEPTED);
            }

            sendNotification(currentUser,
                    "🎉 Bạn đã tham gia thành công nhóm " + group.getTitle(),
                    NotificationType.SYSTEM);

            return inviteMapper.toInviteResponse(savedInvite);
        }

        invite.setStatus(InviteStatus.DECLINED);
        invite.setRespondedAt(LocalDateTime.now());
        GroupInvite savedInvite = groupInviteRepository.save(invite);

        sendNotification(invite.getInviter(),
                "❌ " + currentUser.getFullName() + " đã từ chối lời mời tham gia nhóm " + invite.getGroup().getTitle(),
                NotificationType.INVITE_DECLINED);

        return inviteMapper.toInviteResponse(savedInvite);
    }

    private Pageable buildPageable(int page, int size) {
        int safePage = Math.max(page, 1) - 1;
        int safeSize = Math.max(size, 1);
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private User getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
    }

    private void sendNotification(User receiver, String content, NotificationType type) {
        notificationRepository.save(Notification.builder()
                .receiver(receiver)
                .content(content)
                .type(type)
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build());
    }
}
