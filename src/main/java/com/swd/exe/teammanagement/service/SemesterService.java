package com.swd.exe.teammanagement.service;

import java.util.List;

import com.swd.exe.teammanagement.dto.request.SemesterRequest;
import com.swd.exe.teammanagement.dto.response.SemesterResponse;

public interface SemesterService {
    SemesterResponse createSemester(SemesterRequest request);
    SemesterResponse updateSemester(Long id, SemesterRequest request);
    String changeActiveSemester(Long id);
    SemesterResponse getSemesterById(Long id);
    List<SemesterResponse> getAllSemesters();
    SemesterResponse activateSemester(Long id);
    SemesterResponse deactivateSemester(Long id);
    SemesterResponse changeSemesterActiveStatus(Long id);
    String completeSemester(Long id);
}
