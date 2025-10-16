package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupTeacher;
import com.swd.exe.teammanagement.entity.Semester;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.user.UserRole;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.*;
import com.swd.exe.teammanagement.service.TeacherCheckPointService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class TeacherCheckPointServiceImpl implements TeacherCheckPointService {

    UserRepository userRepository;
    GroupRepository groupRepository;
    GroupTeacherRepository groupTeacherRepository;

    @Override
    public List<User> getAllTeachers() {
        List<User> teachers = userRepository.findByRole(UserRole.LECTURER);
        return teachers;
    }


    @Override
    public Void assignTeacherToGroup(Long groupId, Long teacherId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
        if(groupTeacherRepository.existsByGroup(group)){
            throw new AppException(ErrorCode.TEACHER_ASSIGNED);
        }
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
        Semester semester = group.getSemester();
        List<User> teachers = userRepository.findByRole(UserRole.LECTURER);
        long totalGroups = groupRepository.countBySemester(semester);
        double avgGroupsPerTeacher = (double) totalGroups / teachers.size();
        long assignedGroups = groupTeacherRepository.countByTeacherAndGroup_Semester(teacher, semester);
        if (assignedGroups >= Math.ceil(avgGroupsPerTeacher+1)) {
            throw new AppException(ErrorCode.TEACHER_OVERLOAD);
        }
        GroupTeacher groupTeacher = GroupTeacher.builder()
                .group(group)
                .teacher(teacher)
                .assignedAt(LocalDateTime.now())
                .build();
        groupTeacherRepository.save(groupTeacher);
        return null;
    }
}
