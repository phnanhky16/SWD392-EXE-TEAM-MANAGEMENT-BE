package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.request.SemesterRequest;
import com.swd.exe.teammanagement.dto.response.SemesterResponse;
import com.swd.exe.teammanagement.dto.response.UserResponse;

import java.util.List;

public interface SemesterService {
    SemesterResponse createSemester(SemesterRequest request);
    SemesterResponse updateSemester(Long id, SemesterRequest request);
    Void changeActiveSemester(Long id);
    SemesterResponse getSemesterById(Long id);
    List<SemesterResponse> getAllSemesters();
}
