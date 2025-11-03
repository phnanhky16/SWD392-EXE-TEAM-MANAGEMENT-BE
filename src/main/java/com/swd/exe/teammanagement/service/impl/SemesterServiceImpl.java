package com.swd.exe.teammanagement.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.swd.exe.teammanagement.dto.request.SemesterRequest;
import com.swd.exe.teammanagement.dto.response.SemesterResponse;
import com.swd.exe.teammanagement.entity.Semester;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.mapper.SemesterMapper;
import com.swd.exe.teammanagement.repository.SemesterRepository;
import com.swd.exe.teammanagement.service.SemesterService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class SemesterServiceImpl implements SemesterService {
    SemesterRepository semesterRepository;
    SemesterMapper semesterMapper;
    @Override
    @Transactional
    public SemesterResponse createSemester(SemesterRequest request) {
        if(semesterRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new AppException(ErrorCode.SEMESTER_EXISTED);
        }
        Semester semester = Semester.builder()
                .name(request.getName())
                .active(false)
                .build();
        semesterRepository.save(semester);
        return semesterMapper.toSemesterResponse(semester);
    }

    @Override
    public SemesterResponse updateSemester(Long id, SemesterRequest request) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED));
        semesterMapper.toUpdateSemester(semester, request);
        semesterRepository.save(semester);
        return semesterMapper.toSemesterResponse(semester);
    }

    @Override
    public String changeActiveSemester(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED));
        semester.setActive(!Boolean.TRUE.equals(semester.getActive()));
        semesterRepository.save(semester);
        return "Semester active status changed successfully";
    }

    @Override
    public SemesterResponse getSemesterById(Long id) {
        return semesterMapper.toSemesterResponse(semesterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED)));
    }

    @Override
    public List<SemesterResponse> getAllSemesters() {
        return semesterMapper.toSemesterResponseList(semesterRepository.findAll());
    }

    @Override
    public SemesterResponse activateSemester(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED));
        semester.setActive(true);
        semesterRepository.save(semester);
        return semesterMapper.toSemesterResponse(semester);
    }

    @Override
    public SemesterResponse deactivateSemester(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED));
        semester.setActive(false);
        semesterRepository.save(semester);
        return semesterMapper.toSemesterResponse(semester);
    }

    @Override
    public SemesterResponse changeSemesterActiveStatus(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED));
        semester.setActive(!Boolean.TRUE.equals(semester.getActive()));
        semesterRepository.save(semester);
        return semesterMapper.toSemesterResponse(semester);
    }

}
