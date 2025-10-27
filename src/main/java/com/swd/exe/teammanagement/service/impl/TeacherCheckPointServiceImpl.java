package com.swd.exe.teammanagement.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.swd.exe.teammanagement.dto.response.GroupSummaryResponse;
import com.swd.exe.teammanagement.dto.response.TeacherRequestResponse;
import com.swd.exe.teammanagement.dto.response.UserSummaryResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.GroupTeacher;
import com.swd.exe.teammanagement.entity.Semester;
import com.swd.exe.teammanagement.entity.TeacherRequest;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.teacher.RequestStatus;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import com.swd.exe.teammanagement.enums.user.UserRole;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.GroupMemberRepository;
import com.swd.exe.teammanagement.repository.GroupRepository;
import com.swd.exe.teammanagement.repository.GroupTeacherRepository;
import com.swd.exe.teammanagement.repository.TeacherRequestRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.service.TeacherCheckPointService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class TeacherCheckPointServiceImpl implements TeacherCheckPointService {

    UserRepository userRepository;
    GroupRepository groupRepository;
    GroupTeacherRepository groupTeacherRepository;
    GroupMemberRepository groupMemberRepository;
    TeacherRequestRepository teacherRequestRepository;

    @Override
    public List<User> getAllTeachers() {
        List<User> teachers = userRepository.findByRole(UserRole.LECTURER);
        return teachers;
    }


    @Override
    public Void assignTeacherToGroup(Long teacherId) {
        User currentUser = getCurrentUser();

        GroupMember gm = groupMemberRepository.findByUserAndActiveTrue(currentUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        if (gm.getMembershipRole() != MembershipRole.LEADER) {
            throw new AppException(ErrorCode.ONLY_GROUP_LEADER);
        }

        Group group = gm.getGroup();

        if (!group.getStatus().equals(GroupStatus.LOCKED)) {
            throw new AppException(ErrorCode.GROUP_UNLOCKED);
        }

        if (groupTeacherRepository.existsByGroupAndActiveTrue(group)) {
            throw new AppException(ErrorCode.TEACHER_ASSIGNED);
        }

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));

        teacherRequestRepository.save(TeacherRequest.builder()
                .status(RequestStatus.PENDING)
                .teacher(teacher)
                .group(group)
                .build());
        return null;
    }

    @Override
    public Void moderatorAssignTeacherToGroup(Long groupId, Long teacherId) {
        User currentUser = getCurrentUser();
        
        // Kiểm tra user là MODERATOR
        if (currentUser.getRole() != UserRole.MODERATOR) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

        if (!group.getStatus().equals(GroupStatus.LOCKED)) {
            throw new AppException(ErrorCode.GROUP_UNLOCKED);
        }

        if (groupTeacherRepository.existsByGroupAndActiveTrue(group)) {
            throw new AppException(ErrorCode.TEACHER_ASSIGNED);
        }

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));

        if (teacher.getRole() != UserRole.LECTURER) {
            throw new AppException(ErrorCode.USER_NOT_TEACHER);
        }

        // Kiểm tra load balancing - teacher không được quá tải
        Semester semester = group.getSemester();
        List<User> teachers = userRepository.findByRole(UserRole.LECTURER);
        long totalGroups = groupRepository.countBySemesterAndActiveTrue(semester);
        double avgGroupsPerTeacher = (double) totalGroups / teachers.size();
        long assignedGroups = groupTeacherRepository.countByTeacherAndGroup_SemesterAndActiveTrue(teacher, semester);
        
        if (assignedGroups >= Math.ceil(avgGroupsPerTeacher + 1)) {
            throw new AppException(ErrorCode.TEACHER_OVERLOAD);
        }

        // Tạo TeacherRequest với status ACCEPTED và assign teacher
        TeacherRequest teacherRequest = TeacherRequest.builder()
                .status(RequestStatus.ACCEPTED)
                .teacher(teacher)
                .group(group)
                .build();
        teacherRequestRepository.save(teacherRequest);
        
        GroupTeacher groupTeacher = GroupTeacher.builder()
                .group(group)
                .teacher(teacher)
                .assignedAt(LocalDateTime.now())
                .build();
        groupTeacherRepository.save(groupTeacher);
        
        return null;
    }

    @Override
    public void teacherResponseToGroup(Long requestId, boolean isAccepted) {
        TeacherRequest request = teacherRequestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_UNEXISTED));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new AppException(ErrorCode.REQUEST_ALREADY_RESPONDED);
        }
        Semester semester = request.getGroup().getSemester();
        List<User> teachers = userRepository.findByRole(UserRole.LECTURER);
        long totalGroups = groupRepository.countBySemesterAndActiveTrue(semester);
        double avgGroupsPerTeacher = (double) totalGroups / teachers.size();
        long assignedGroups = groupTeacherRepository.countByTeacherAndGroup_SemesterAndActiveTrue(request.getTeacher(), semester);
        if (assignedGroups >= Math.ceil(avgGroupsPerTeacher+1)) {
            throw new AppException(ErrorCode.TEACHER_OVERLOAD);
        }
        if (isAccepted) {
            request.setStatus(RequestStatus.ACCEPTED);
            GroupTeacher groupTeacher = GroupTeacher.builder()
                    .group(request.getGroup())
                    .teacher(request.getTeacher())
                    .assignedAt(LocalDateTime.now())
                    .build();
            groupTeacherRepository.save(groupTeacher);
        } else {
            request.setStatus(RequestStatus.REJECTED);
        }
        teacherRequestRepository.save(request);
    }

    @Override
    public List<TeacherRequest> getPendingRequestsForTeacher() {
        User currentUser = getCurrentUser();
        return teacherRequestRepository.findByTeacherAndStatus(currentUser, RequestStatus.PENDING);
    }
    
    @Override
    public List<GroupResponse> getGroupsRejected() {
        User currentUser = getCurrentUser();
        List<TeacherRequest> rejectedRequests = teacherRequestRepository.findByTeacherAndStatus(currentUser, RequestStatus.REJECTED);
        return rejectedRequests.stream()
                .map(TeacherRequest::getGroup)
                .filter(g -> !groupTeacherRepository.existsByGroupAndActiveTrue(g))
                .map(g -> GroupResponse.builder()
                        .id(g.getId())
                        .title(g.getTitle())
                        .description(g.getDescription())
                        .semester(g.getSemester())
                        .status(g.getStatus())
                        .type(g.getType())
                        .createdAt(g.getCreatedAt())
                        .build())
                .toList();
    }
    
    @Override
    public List<GroupResponse> getGroupsUnregistered() {
        User currentUser = getCurrentUser();
        
        // Lấy tất cả groups LOCKED chưa có teacher
        List<Group> allLockedGroupsWithoutTeacher = groupRepository.findAll().stream()
                .filter(g -> g.getStatus() == GroupStatus.LOCKED)
                .filter(g -> !groupTeacherRepository.existsByGroupAndActiveTrue(g))
                .toList();
        
        // Lấy danh sách groupIds mà teacher này đã request
        List<Long> requestedGroupIds = teacherRequestRepository.findAll().stream()
                .filter(tr -> tr.getTeacher().getId().equals(currentUser.getId()))
                .map(tr -> tr.getGroup().getId())
                .toList();
        
        // Lấy groups chưa đăng ký (không có trong teacherRequest)
        return allLockedGroupsWithoutTeacher.stream()
                .filter(g -> !requestedGroupIds.contains(g.getId()))
                .map(g -> GroupResponse.builder()
                        .id(g.getId())
                        .title(g.getTitle())
                        .description(g.getDescription())
                        .semester(g.getSemester())
                        .status(g.getStatus())
                        .type(g.getType())
                        .createdAt(g.getCreatedAt())
                        .build())
                .toList();
    }
    
    @Override
    public List<GroupResponse> getGroupsAccepted() {
        User currentUser = getCurrentUser();
        List<TeacherRequest> acceptedRequests = teacherRequestRepository.findByTeacherAndStatus(currentUser, RequestStatus.ACCEPTED);
        return acceptedRequests.stream()
                .map(TeacherRequest::getGroup)
                .map(g -> GroupResponse.builder()
                        .id(g.getId())
                        .title(g.getTitle())
                        .description(g.getDescription())
                        .semester(g.getSemester())
                        .status(g.getStatus())
                        .type(g.getType())
                        .createdAt(g.getCreatedAt())
                        .build())
                .toList();
    }
    
    private User getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
    }
    @Override
    public TeacherRequestResponse getMyRequestTeacherCheckpoints(Long groupId) {
        User me = getCurrentUser();

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

        // chỉ cho member active xem
        boolean isMember = groupMemberRepository.existsByGroupAndUserAndActiveTrue(group, me);
        if (!isMember) throw new AppException(ErrorCode.USER_NOT_IN_GROUP);

        // 1) Nếu group đã có teacher đang gán → coi như ACCEPTED
        var gtOpt = groupTeacherRepository.findByGroupAndActiveTrue(group);
        if (gtOpt.isPresent()) {
            var gt = gtOpt.get();
            return TeacherRequestResponse.builder()
                    .requestId(null) // là trạng thái gán, không phải request cụ thể
                    .group(GroupSummaryResponse.builder()
                            .id(group.getId())
                            .title(group.getTitle())
                            .build())
                    .teacher(UserSummaryResponse.builder()
                            .id(gt.getTeacher().getId())
                            .fullName(gt.getTeacher().getFullName())
                            .email(gt.getTeacher().getEmail())
                            .build())
                    .status(RequestStatus.ACCEPTED)
                    .message(null)
                    .build();
        }

        // 2) Chưa gán → lấy request MỚI NHẤT (PENDING / REJECTED / …)
        var reqOpt = teacherRequestRepository.findTopByGroup_IdOrderByIdDesc(groupId);
        // nếu bạn không có createdAt, đổi sang OrderByIdDesc ở repository
        return reqOpt.map(req ->
                TeacherRequestResponse.builder()
                        .requestId(req.getId())
                        .group(GroupSummaryResponse.builder()
                                .id(group.getId())
                                .title(group.getTitle())
                                .build())
                        .teacher(UserSummaryResponse.builder()
                                .id(req.getTeacher().getId())
                                .fullName(req.getTeacher().getFullName())
                                .email(req.getTeacher().getEmail())
                                .build())
                        .status(req.getStatus())
                        .message(null)
                        .build()
        ).orElseGet(() ->
                TeacherRequestResponse.builder()
                        .requestId(null)
                        .group(GroupSummaryResponse.builder()
                                .id(group.getId())
                                .title(group.getTitle())
                                .build())
                        .teacher(null)
                        .status(null)
                        .message("Nhóm chưa chọn giáo viên chấm checkpoints")
                        .build()
        );
    }
}
