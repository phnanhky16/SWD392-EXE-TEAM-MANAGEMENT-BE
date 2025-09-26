package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.request.MajorRequest;
import com.swd.exe.teammanagement.dto.response.MajorResponse;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.mapper.MajorMapper;
import com.swd.exe.teammanagement.repository.MajorRepository;
import com.swd.exe.teammanagement.service.MajorService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class MajorServiceImpl implements MajorService {
    MajorRepository majorRepository;
    MajorMapper majorMapper;

    @Override
    public MajorResponse createMajor(MajorRequest request) {
        if(majorRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.MAJOR_EXISTED);
        }
        Major major = majorMapper.toMajor(request);
        return majorMapper.toMajorResponse(majorRepository.save(major));
    }

    @Override
    public MajorResponse updateMajor(String code, MajorRequest request) {
        Major major = majorRepository.findById(code).orElseThrow(()-> new AppException(ErrorCode.MAJOR_UNEXISTED));
            majorMapper.toUpdateMajor(major,request);
            majorRepository.save(major);
            return majorMapper.toMajorResponse(major);
    }

    @Override
    public Void deleteMajor(String code) {
        Major major = majorRepository.findById(code)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_UNEXISTED));
        majorRepository.delete(major);
        return null;
    }

    @Override
    public MajorResponse getMajorByCode(String code) {
        return majorMapper.toMajorResponse(majorRepository.findById(code)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_UNEXISTED)));
    }

    @Override
    public List<MajorResponse> getAllMajors() {
        return majorMapper.toMajorResponseList(majorRepository.findAll());
    }
}
