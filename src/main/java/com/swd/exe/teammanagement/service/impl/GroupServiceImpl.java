package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.dto.response.PagingResponse;
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
import com.swd.exe.teammanagement.mapper.GroupMapper;
import com.swd.exe.teammanagement.repository.*;
import com.swd.exe.teammanagement.service.GroupService;
import com.swd.exe.teammanagement.spec.GroupSpecs;
import com.swd.exe.teammanagement.util.PageUtil;
import com.swd.exe.teammanagement.util.SortUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class GroupServiceImpl implements GroupService {
    GroupRepository groupRepository;
    UserRepository userRepository;
    GroupMemberRepository groupMemberRepository;
    PostRepository postRepository;
    IdeaRepository ideaRepository;
    VoteRepository voteRepository;
    GroupMapper groupMapper;
    JoinRepository joinRepository;


    @Override
    public GroupResponse getGroupById(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_UNEXISTED));
        return GroupResponse.builder()
                .id(group.getId())
                .title(group.getTitle())
                .description(group.getDescription())
                .leader(group.getLeader())
                .type(group.getType()).status(group.getStatus())
                .checkpointTeacher(group.getCheckpointLecture())
                .createdAt(group.getCreatedAt())
                .build();
    }

    @Override
    public List<GroupResponse> getAllGroups() {
        List<Group> groups = groupRepository.findAll();

        return groups.stream().map(group -> GroupResponse.builder()
                .id(group.getId())
                .title(group.getTitle())
                .description(group.getDescription())
                .leader(group.getLeader())
                .type(group.getType()).status(group.getStatus())
                .checkpointTeacher(group.getCheckpointLecture())
                .build()
        ).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<GroupResponse> searchGroups(
            String q, GroupStatus status, GroupType type,
            int page, int size, String sort, String dir) {

        Sort s = SortUtil.sanitize(sort, dir,
                Set.of("id", "title", "status", "type"),
                "id", Sort.Direction.DESC);

        Pageable pageable = SortUtil.pageable1Based(page, size, s);

        Specification<Group> spec = Specification.allOf(
                GroupSpecs.keyword(q),
                GroupSpecs.status(status),
                GroupSpecs.type(type)
        );

        Page<Group> p = groupRepository.findAll(spec, pageable);

        return PageUtil.toResponse(p, groupMapper::toGroupResponse);
    }

    @Override
    public List<GroupResponse> getCurrentGroupList() {
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
        List<Group> groups = groupRepository.findGroupsByCreatedAtBetween(startDate,endDate);
        return groups.stream().map(group -> GroupResponse.builder()
                .id(group.getId())
                .title(group.getTitle())
                .description(group.getDescription())
                .leader(group.getLeader())
                .type(group.getType()).status(group.getStatus())
                .checkpointTeacher(group.getCheckpointLecture())
                .build()
        ).toList();
    }

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
    public GroupResponse getMyGroup() {
        User user = getCurrentUser();
        GroupMember groupMember = groupMemberRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
        Group group = groupMember.getGroup();
        return GroupResponse.builder()
                .id(group.getId())
                .title(group.getTitle())
                .description(group.getDescription())
                .leader(group.getLeader())
                .type(group.getType()).status(group.getStatus())
                .checkpointTeacher(group.getCheckpointLecture())
                .build();
    }

    @Override
    public Void removeMemberByLeader(Long userId) {
        User leader = getCurrentUser();
        GroupMember leaderMember = groupMemberRepository.findByUser(leader)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
        if (!leaderMember.getRole().equals(MembershipRole.LEADER)) {
            throw new AppException(ErrorCode.ONLY_GROUP_LEADER);
        }
        User memberToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
        GroupMember member = groupMemberRepository.findByUser(memberToRemove)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
        groupMemberRepository.delete(member);
        joinRepository.deleteJoinByFromUser(memberToRemove);
        return null;
    }

    public Void deleteGroup(Long groupId) {
        Group g = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_UNEXISTED));
        int size = getCurrentGroupList().size();
        g.setLeader(null);
        g.setTitle("Group exe #"+ size+1);
        g.setDescription(null);
        g.setType(GroupType.PUBLIC);
        g.setStatus(GroupStatus.FORMING);
        groupRepository.save(g);
        postRepository.deletePostByGroup(g);
        ideaRepository.deleteIdeaByGroup(g);
        voteRepository.deleteVotesByGroup(g);
        joinRepository.deleteJoinsByToGroup(g);
        return null;
    }

    @Override
    public Void changeGroupType() {
        Group g = groupRepository.findByLeader(getCurrentUser())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_LEADER_OF_ANY_GROUP));
        g.setType(!g.getType().equals(GroupType.PUBLIC) ? GroupType.PUBLIC : GroupType.PRIVATE);
        groupRepository.save(g);
        return null;
    }

    @Override
    public GroupResponse getGroupByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
        GroupMember groupMember = groupMemberRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
        Group group = groupMember.getGroup();
        return GroupResponse.builder()
                .id(group.getId())
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
        Group  group = groupMember.getGroup();
        List<GroupMember> gMS  = groupMemberRepository.findByGroup(group);
        if(gMS.size()==1){
            deleteGroup(group.getId());
        }else{
            if (groupMember.getRole().equals(MembershipRole.LEADER)) {
                gMS.get(2).setRole(MembershipRole.LEADER);
                User member = gMS.get(2).getUser();
                group.setLeader(member);
                groupRepository.save(group);
                groupMemberRepository.delete(groupMember);
                joinRepository.deleteJoinByFromUser(user);
            }
        }
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
        List<Group> groups = groupRepository.findGroupsByStatusInAndCreatedAtBetween(
                Arrays.asList(GroupStatus.ACTIVE, GroupStatus.FORMING), startDate, endDate
        );
        List<Group> groupListAvailable = new ArrayList<>();
        Set<Major> majors = new HashSet<>();
        for( Group g : groups){
            if(groupMemberRepository.countByGroup(g) ==5) {
                for (User member : groupMemberRepository.findUsersByGroup(g)) {
                    majors.add(member.getMajor());
                }
                majors.add(user.getMajor());
                if (majors.size() > 1) {
                    groupListAvailable.add(g);
                }
                majors.clear();
            }else{ groupListAvailable.add(g); }
        }
        return groupListAvailable.stream().map(group -> GroupResponse.builder()
                .id(group.getId())
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
        int MAX_SIZE = 6;
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
        int count = getCurrentGroupList().size();
        for (int i = count+1; i < size+count; i++) {
            String title = "Group EXE " + semester + " " + year + " #" + i;
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
