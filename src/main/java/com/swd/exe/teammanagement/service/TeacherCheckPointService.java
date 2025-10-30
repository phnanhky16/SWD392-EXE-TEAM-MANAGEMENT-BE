package com.swd.exe.teammanagement.service;

import java.util.List;

import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.dto.response.TeacherRequestResponse;
import com.swd.exe.teammanagement.entity.User;

public interface TeacherCheckPointService {
    List<User> getAllTeachers();
    Void assignTeacherToGroup(Long teacherId);
    Void moderatorAssignTeacherToGroup(Long groupId, Long teacherId);
    void teacherResponseToGroup(Long requestId, boolean isAccepted);
    List<TeacherRequestResponse> getPendingRequestsForTeacher();
    List<GroupResponse> getGroupsRejected();
    List<GroupResponse> getGroupsUnregistered();
    List<GroupResponse> getGroupsAccepted();
    TeacherRequestResponse getMyRequestTeacherCheckpoints(Long groupId);
}
