package com.swd.exe.teammanagement.service;

import java.util.List;

import com.swd.exe.teammanagement.dto.request.MajorRequest;
import com.swd.exe.teammanagement.dto.response.MajorResponse;

public interface MajorService {
    MajorResponse createMajor(MajorRequest request);
    MajorResponse updateMajor(Long id, MajorRequest request);
    String deleteMajor(Long id);
    MajorResponse getMajorById(Long id);
    List<MajorResponse> getAllMajors();
    MajorResponse activateMajor(Long id);
    MajorResponse deactivateMajor(Long id);
    MajorResponse changeMajorActiveStatus(Long id);

}
