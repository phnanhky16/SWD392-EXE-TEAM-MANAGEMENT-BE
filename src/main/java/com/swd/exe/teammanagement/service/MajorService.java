package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.request.MajorRequest;
import com.swd.exe.teammanagement.dto.response.MajorResponse;

import java.util.List;

public interface MajorService {
    MajorResponse createMajor(MajorRequest request);
    MajorResponse updateMajor(String code, MajorRequest request);
    Void deleteMajor(String code);
    MajorResponse getMajorByCode(String code);
    List<MajorResponse> getAllMajors();

}
