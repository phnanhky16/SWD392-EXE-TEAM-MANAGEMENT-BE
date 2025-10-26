package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.request.SemesterRequest;
import com.swd.exe.teammanagement.dto.response.SemesterResponse;
import com.swd.exe.teammanagement.entity.Semester;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.mapper.SemesterMapper;
import com.swd.exe.teammanagement.repository.SemesterRepository;
import com.swd.exe.teammanagement.service.SemesterService;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Void changeActiveSemester(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED));
        semester.setActive(!Boolean.TRUE.equals(semester.getActive()));
        semesterRepository.save(semester);
        return null;
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

}
