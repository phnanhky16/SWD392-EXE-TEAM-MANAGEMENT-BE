package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.request.GroupCreateRequest;
import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.Post;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.*;
import com.swd.exe.teammanagement.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class GroupServiceImpl implements GroupService {
    GroupRepository groupRepository;
    UserRepository userRepository;
    GroupMemberRepository groupMemberRepository;
    int MAX_SIZE = 6;
    private final PostRepository postRepository;
    private final IdeaRepository ideaRepository;

    @Override
    public GroupResponse createGroup(GroupCreateRequest request) {
        if(groupMemberRepository.existsByUser(getCurrentUser())){
            throw new AppException(ErrorCode.USER_ALREADY_IN_GROUP);
        }
        if (request.getInviteeEmails() == null || request.getInviteeEmails().size() != 2) {
            throw new AppException(ErrorCode.CREATE_GROUP_NEED_INVITE_2_MEMBERS);
        }
        List<String> inviteeEmails = request.getInviteeEmails();
        Set<String> uniq = new HashSet<>(inviteeEmails);
        if (uniq.size() != 2) throw new AppException(ErrorCode.INVITEE_MUST_BE_DISTINCT);
        if (inviteeEmails.contains(getCurrentUser().getEmail())) throw new AppException(ErrorCode.CANNOT_INVITE_CREATOR_AS_INVITEE);
        Group group = Group.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .leader(getCurrentUser())
                .type(GroupType.PUBLIC)
                .status(GroupStatus.ACTIVE)
                .build();
        group = groupRepository.save(group);
        groupMemberRepository.save(
                GroupMember.builder()
                        .group(group)
                        .user(getCurrentUser())
                        .role(MembershipRole.LEADER)
                        .build()
        );
        for (String inviteeEmail : inviteeEmails) {
            User invitee = userRepository.findByEmail(inviteeEmail)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
            if(groupMemberRepository.existsByUser(invitee)){
                throw new AppException(ErrorCode.USER_ALREADY_IN_GROUP);
            }
            groupMemberRepository.save(
                    GroupMember.builder()
                            .group(group)
                            .user(invitee)
                            .role(MembershipRole.MEMBER)
                            .build()
            );
        }
        return GroupResponse.builder()
                .title(group.getTitle())
                .description(group.getDescription())
                .leader(getCurrentUser())
                .type(group.getType())
                .status(group.getStatus())
                .build();
    }

    @Override
    public GroupResponse getGroupById(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_UNEXISTED));
        return GroupResponse.builder()
                .title(group.getTitle())
                .description(group.getDescription())
                .leader(group.getLeader())
                .type(group.getType()).status(group.getStatus())
                .checkpointTeacher(group.getCheckpointTeacher())
                .build();
    }

    @Override
    public List<GroupResponse> getAllGroups() {
        List<Group> groups = groupRepository.findAll();

        return groups.stream().map(group -> GroupResponse.builder()
                .title(group.getTitle())
                .description(group.getDescription())
                .leader(group.getLeader())
                .type(group.getType()).status(group.getStatus())
                .checkpointTeacher(group.getCheckpointTeacher())
                .build()
        ).toList();
    }

    @Override
    public Void deleteGroup() {
        User u = getCurrentUser();
        Group g = groupRepository.findByLeader(u)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_LEADER_OF_ANY_GROUP));
        if(!groupMemberRepository.existsByGroupIdAndUserIdAndRole(g.getId(), u.getId(), MembershipRole.LEADER)){
            throw new AppException(ErrorCode.ONLY_GROUP_LEADER);
        }
        groupRepository.deleteGroupByLeader(u);
        List<User> members = groupMemberRepository.findUsersByGroup(g);
        for (User member : members) {
            groupMemberRepository.deleteGroupMemberByUser(member);
        }
        postRepository.deletePostByGroup(g);
        ideaRepository.deleteIdeaByGroup(g);
        return null;
    }

    @Override
    public GroupResponse changeGroupType() {
        Group g = groupRepository.findByLeader(getCurrentUser())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_LEADER_OF_ANY_GROUP));
        g.setType(!g.getType().equals(GroupType.PUBLIC) ? GroupType.PUBLIC : GroupType.PRIVATE);
        return null;
    }

    @Override
    public GroupResponse getGroup(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
        GroupMember groupMember = groupMemberRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
        Group group = groupMember.getGroup();
        return GroupResponse.builder()
                .title(group.getTitle())
                .description(group.getDescription())
                .leader(group.getLeader())
                .type(group.getType()).status(group.getStatus())
                .checkpointTeacher(group.getCheckpointTeacher())
                .build();
    }

    @Override
    public Void leaveGroup() {
        User user = getCurrentUser();
        GroupMember groupMember = groupMemberRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
        if (groupMember.getRole().equals(MembershipRole.LEADER)) {
            throw new AppException(ErrorCode.GROUP_LEADER_CANNOT_LEAVE);
        }
        groupMemberRepository.delete(groupMember);
        return null;
    }

    @Override
    public List<GroupResponse> getAvailableGroups() {
        User user = getCurrentUser();
        List<Group> gs = groupRepository.findAll();
        //th 5ng: 1 chuyên ngành check chueen ngành
        return List.of();
    }

    @Override
    public Void doneTeam() {
            User user = getCurrentUser();
            GroupMember groupMember = groupMemberRepository.findByUser(user)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
            Group group = groupMember.getGroup();
            if (!groupMember.getRole().equals(MembershipRole.LEADER)) {
                throw new AppException(ErrorCode.ONLY_GROUP_LEADER);
            }
            int memberCount = groupMemberRepository.countByGroup(group);
            if (memberCount != MAX_SIZE) {
                throw new AppException(ErrorCode.GROUP_SHOULD_ENOUGH_MEMBERS);
            }
            group.setStatus(GroupStatus.LOCKED);
            groupRepository.save(group);
        return null;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
    }
}
