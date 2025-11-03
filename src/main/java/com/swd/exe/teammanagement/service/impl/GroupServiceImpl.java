package com.swd.exe.teammanagement.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.swd.exe.teammanagement.enums.user.UserRole;
import com.swd.exe.teammanagement.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swd.exe.teammanagement.dto.request.GroupCreateRequest;
import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.dto.response.PagingResponse;
import com.swd.exe.teammanagement.dto.response.UserResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.entity.Semester;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.mapper.GroupMapper;
import com.swd.exe.teammanagement.service.GroupService;
import com.swd.exe.teammanagement.spec.GroupSpecs;
import com.swd.exe.teammanagement.util.PageUtil;
import com.swd.exe.teammanagement.util.SortUtil;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

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
    SemesterRepository semesterRepository;
    GroupTeacherRepository groupTeacherRepository;
    @Override
    public GroupResponse getGroupById(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_UNEXISTED));
        return mapToResponse(group);
    }

    @Override
    public List<GroupResponse> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse getGroupLeader(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_UNEXISTED));

        GroupMember leaderMember = groupMemberRepository
                .findByGroupAndMembershipRoleAndActiveTrue(group, MembershipRole.LEADER)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_LEADER_NOT_FOUND));

        return mapToUserResponse(leaderMember.getUser());
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
        Semester semester = semesterRepository.findByActiveTrue();
        List<Group> groups = groupRepository.findGroupsBySemesterAndActiveTrue(semester);
        return groups.stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<UserResponse> getMembersByGroupId(Long groupId) {
        List<User> users = groupMemberRepository.findUsersByGroupId(groupId);
        return users.stream().map(this::mapToUserResponse).toList();
    }

    @Override
    public int getGroupMemberCount(Long groupId) {
        return groupMemberRepository.countByGroupIdAndActiveTrue(groupId);
    }

    @Override
    public Set<Major> getMajorDistribution(Long groupId) {
        return new HashSet<>(groupMemberRepository.findMajorsByGroupId(groupId));
    }

    @Override
    public GroupResponse getMyGroup() {
        GroupMember gm = groupMemberRepository.findByUserAndActiveTrue(getCurrentUser())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
        return mapToResponse(gm.getGroup());
    }

    @Override
    public Void removeMemberByLeader(Long userId) {
        User leader = getCurrentUser();
        GroupMember leaderMember = groupMemberRepository.findByUserAndActiveTrue(leader)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
        if (!leaderMember.getMembershipRole().equals(MembershipRole.LEADER)) {
            throw new AppException(ErrorCode.ONLY_GROUP_LEADER);
        }

        User member = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));

        GroupMember gm = groupMemberRepository.findByUserAndActiveTrue(member)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
        if(gm.getGroup().getStatus().equals(GroupStatus.LOCKED)){
            throw new AppException(ErrorCode.GROUP_LOCKED_CANNOT_LEAVE);
        }
        gm.setActive(false);
        groupMemberRepository.save(gm);
        joinRepository.deactivateJoinsByFromUser(member);
        return null; // keep Void for compatibility
    }

    @Override
    public GroupResponse updateGroupInfo(GroupCreateRequest request) {
        User user = getCurrentUser();
        GroupMember gm = groupMemberRepository.findByUserAndActiveTrue(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        if (!gm.getMembershipRole().equals(MembershipRole.LEADER)) {
            throw new AppException(ErrorCode.ONLY_GROUP_LEADER);
        }

        Group group = gm.getGroup();
        group.setTitle(request.getTitle());
        group.setDescription(request.getDescription());
        groupRepository.save(group);

        return mapToResponse(group);
    }

    @Override
    public Void changeLeader(Long newLeaderId) {
        User currentLeader = getCurrentUser();
        GroupMember leaderMember = groupMemberRepository.findByUserAndActiveTrue(currentLeader)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        if (!leaderMember.getMembershipRole().equals(MembershipRole.LEADER)) {
            throw new AppException(ErrorCode.ONLY_GROUP_LEADER);
        }

        Group group = leaderMember.getGroup();
        User newLeader = userRepository.findById(newLeaderId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));

        GroupMember newLeaderMember = groupMemberRepository.findByUserAndActiveTrue(newLeader)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        if (!newLeaderMember.getGroup().getId().equals(group.getId())) {
            throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
        }

        if (currentLeader.getId().equals(newLeaderId)) {
            throw new AppException(ErrorCode.CANNOT_TRANSFER_TO_SELF);
        }

        leaderMember.setMembershipRole(MembershipRole.MEMBER);
        newLeaderMember.setMembershipRole(MembershipRole.LEADER);

        groupMemberRepository.saveAll(List.of(leaderMember, newLeaderMember));
        return null;
    }

    @Override
    public List<GroupResponse> getGroupsBySemester(Long semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED));
        List<Group> groups = groupRepository.findGroupsBySemesterAndActiveTrue(semester);
        return groups.stream().map(this::mapToResponse).toList();
    }

    @Override
    public Void changeGroupType() {
        GroupMember gm = groupMemberRepository.findByUserAndActiveTrue(getCurrentUser())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
        if (gm.getMembershipRole().equals(MembershipRole.MEMBER)) {
            throw new AppException(ErrorCode.ONLY_GROUP_LEADER);
        }
        Group group = gm.getGroup();
        group.setType(group.getType() == GroupType.PUBLIC ? GroupType.PRIVATE : GroupType.PUBLIC);
        groupRepository.save(group);
        return null;
    }

    @Override
    public GroupResponse getGroupByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
        GroupMember gm = groupMemberRepository.findByUserAndActiveTrue(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
        return mapToResponse(gm.getGroup());
    }

    @Override
    public Void leaveGroup() {
        User user = getCurrentUser();
        GroupMember gm = groupMemberRepository.findByUserAndActiveTrue(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        Group group = gm.getGroup();
        if(group.getStatus().equals(GroupStatus.LOCKED)){
            throw new AppException(ErrorCode.GROUP_LOCKED_CANNOT_LEAVE);
        }
        List<GroupMember> members = groupMemberRepository.findByGroupAndActiveTrue(group);
        if (members.size() == 1) {
            resetGroup(group);
        } else {
            handleLeaveWhenNotAlone(gm, members, user);
        }
        return null;
    }

    @Override
    public List<GroupResponse> getAvailableGroups() {
        User user = getCurrentUser();
        Semester semester = semesterRepository.findByActiveTrue();
        List<Group> groups = groupRepository.findGroupsByStatusInAndSemesterAndActiveTrue(
                List.of(GroupStatus.ACTIVE, GroupStatus.FORMING), semester
        );

        List<Group> available = new ArrayList<>();
        Set<Major> majors = new HashSet<>();

        for (Group g : groups) {
            if (groupMemberRepository.countByGroupAndActiveTrue(g) == 5) {
                for (User m : groupMemberRepository.findUsersByGroup(g)) {
                    majors.add(m.getMajor());
                }
                majors.add(user.getMajor());
                if (majors.size() > 1) {
                    available.add(g);
                }
                majors.clear();
            } else {
                available.add(g);
            }
        }
        return available.stream().map(this::mapToResponse).toList();
    }

    @Override
    public Void doneTeam() {
        GroupMember gm = groupMemberRepository.findByUserAndActiveTrue(getCurrentUser())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        if (!gm.getMembershipRole().equals(MembershipRole.LEADER)) {
            throw new AppException(ErrorCode.ONLY_GROUP_LEADER);
        }
        Group group = gm.getGroup();
        Set<Major> majors = getMajorDistribution(group.getId()) ;
        if(majors.size() < 2){
            throw new AppException(ErrorCode.GROUP_NOT_DIVERSE);
        }
        List<GroupMember> gms = groupMemberRepository.findByGroupAndActiveTrue(group);
        if(gms.size() < 1 || gms.size() > 6){
            throw  new AppException(ErrorCode.GROUP_NOT_ENOUGH_MEMBER);
        }
        group.setStatus(GroupStatus.LOCKED);
        groupRepository.save(group);
        postRepository.deactivatePostsByGroup(group);
        return null;
    }

    @Override
    public Void createGroup(int size,long semesterId) {
        if (size <= 0) size = 1;
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED));
        if(semester.getActive()==false){
            throw new AppException(ErrorCode.SEMESTER_NOT_ACTIVE);
        }
        int count = getGroupsBySemester(semesterId).size();
        for (int i = count + 1; i <= size + count; i++) {
            String title = "Group EXE " + semester.getName() + " #" + i;
            Group group = Group.builder()
                    .title(title)
                    .description("Empty group created in " + semester.getName() + " semester")
                    .semester(semester)
                    .type(GroupType.PUBLIC)
                    .status(GroupStatus.FORMING)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            groupRepository.save(group);
        }
        return null;
    }

    // ===== PRIVATE HELPERS =====

    private void handleLeaveWhenNotAlone(GroupMember gm, List<GroupMember> members, User user) {
        if (gm.getMembershipRole().equals(MembershipRole.LEADER)) {
            members.get(1).setMembershipRole(MembershipRole.LEADER);
        }
        gm.setActive(false);
        groupMemberRepository.save(gm);
        joinRepository.deactivateJoinsByFromUser(user);
    }

    private void resetGroup(Group group) {
        group.setTitle("Group EXE " + group.getSemester().getName());
        group.setDescription(null);
        group.setType(GroupType.PUBLIC);
        group.setStatus(GroupStatus.FORMING);
        groupRepository.save(group);
        groupMemberRepository.deactivateGroupMembersByGroup(group);
        postRepository.deactivatePostsByGroup(group);
        ideaRepository.deleteIdeasByGroup(group);
        voteRepository.deactivateVotesByGroup(group);
        joinRepository.deactivateJoinsByToGroup(group);
    }


    private GroupResponse mapToResponse(Group g) {
        return GroupResponse.builder()
                .id(g.getId())
                .title(g.getTitle())
                .description(g.getDescription())
                .semester(g.getSemester())
                .status(g.getStatus())
                .type(g.getType())
                .createdAt(g.getCreatedAt())
                .build();
    }

    private UserResponse mapToUserResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .studentCode(u.getStudentCode())
                .major(u.getMajor())
                .role(u.getRole())
                .isActive(u.getIsActive())
                .build();
    }

    @Override
    public GroupResponse activateGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
        group.setActive(true);
        groupRepository.save(group);
        return mapToResponse(group);
    }

    @Override
    public GroupResponse deactivateGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
        group.setActive(false);
        groupRepository.save(group);
        return mapToResponse(group);
    }

    @Override
    public GroupResponse changeGroupActiveStatus(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
        group.setActive(!group.isActive());
        groupRepository.save(group);
        return mapToResponse(group);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupResponse> getMyAssignedGroups(int page, int size, boolean includeHistory) {
        User me = getCurrentUser();
        if (me.getRole() != UserRole.LECTURER) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), Math.max(1, size));


        Page<Group> groups = includeHistory
                ? groupTeacherRepository.findAllGroupsByTeacher(me, pageable)
                : groupTeacherRepository.findActiveGroupsByTeacher(me, pageable);

        return groups.map(groupMapper::toGroupResponse);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
    }

}
