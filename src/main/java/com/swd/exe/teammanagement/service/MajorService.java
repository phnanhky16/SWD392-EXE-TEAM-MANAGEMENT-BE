package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.request.MajorRequest;
import com.swd.exe.teammanagement.dto.response.MajorResponse;

import java.util.List;

public interface MajorService {
    MajorResponse createMajor(MajorRequest request);
    MajorResponse updateMajor(Long id, MajorRequest request);
    Void deleteMajor(Long id);
    MajorResponse getMajorById(Long id);
    List<MajorResponse> getAllMajors();

}
