package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;
import com.swd.exe.teammanagement.enums.group.Semester;
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
import java.util.ArrayList;
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
    private final VoteRepository voteRepository;
    private final JoinRepository joinRepository;


    @Override
    public GroupResponse getGroupById(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_UNEXISTED));
        return GroupResponse.builder()
                .title(group.getTitle())
                .description(group.getDescription())
                .leader(group.getLeader())
                .type(group.getType()).status(group.getStatus())
                .checkpointTeacher(group.getCheckpointLecture())
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
                .checkpointTeacher(group.getCheckpointLecture())
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
        voteRepository.deleteVotesByGroup(g);
        joinRepository.deleteJoinsByToGroup(g);
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
                .checkpointTeacher(group.getCheckpointLecture())
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

        LocalDateTime now = LocalDateTime.now();
        int month = now.getMonthValue();
        int year = now.getYear();
        int startMonth;
        int endMonth;
        if (month >= 1 && month <= 4) {
            startMonth = 1;
            endMonth = 4;
        } else if (month >= 5 && month <= 8) {
            startMonth = 5;
            endMonth = 8;
        } else {
            startMonth = 9;
            endMonth = 12;
        }

        java.time.YearMonth endYearMonth = java.time.YearMonth.of(year, endMonth);
        LocalDateTime startDate = LocalDateTime.of(year, startMonth, 1, 0, 0);
        LocalDateTime endDate = endYearMonth.atEndOfMonth().atTime(23, 59, 59);

        // fetch groups created within this semester window with ACTIVE status and PUBLIC type
        List<Group> groups = groupRepository.findGroupsByStatusAndTypeAndCreatedAtBetween(
                GroupStatus.ACTIVE, GroupType.PUBLIC, startDate, endDate
        );
        List<Group> groupListAvaiable = new ArrayList<>();
        Set<Major> majors = new HashSet<>();
        for( Group g : groups){
            if(groupMemberRepository.countByGroup(g) ==5) {
                for (User member : groupMemberRepository.findUsersByGroup(g)) {
                    majors.add(member.getMajor());
                }
                majors.add(user.getMajor());
                if (majors.size() > 1) {
                    groupListAvaiable.add(g);
                }
                majors.clear();
            }else{ groupListAvaiable.add(g); }
        }
        return groupListAvaiable.stream().map(group -> GroupResponse.builder()
                .title(group.getTitle())
                .description(group.getDescription())
                .leader(group.getLeader())
                .type(group.getType()).status(group.getStatus())
                .checkpointTeacher(group.getCheckpointLecture())
                .build()
        ).toList();
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

    @Override
    public Void createGroup(int size) {
        if (size <= 0) size = 1;

        int month = LocalDateTime.now().getMonthValue();
        Semester semester;
        if (month >= 1 && month <= 4) {
            semester = Semester.SPRING;
        } else if (month >= 5 && month <= 8) {
            semester = Semester.SUMMER;
        } else {
            semester = Semester.FALL;
        }
        int year = LocalDateTime.now().getYear();
        for (int i = 0; i < size; i++) {
            String title = "Group EXE " + semester + " " + year + (size > 1 ? " #" + (i + 1) : "");
            Group group = Group.builder()
                    .title(title)
                    .description("Empty group created in " + semester + " semester")
                    .type(GroupType.PUBLIC)
                    .status(GroupStatus.FORMING)
                    .createdAt(LocalDateTime.now())
                    .build();
            groupRepository.save(group);
        }
        return null;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
    }
}
