package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.entity.User;

import java.util.List;

public interface TeacherCheckPointService {
    List<User> getAllTeachers();
    Void assignTeacherToGroup(Long groupId, Long teacherId);
}
