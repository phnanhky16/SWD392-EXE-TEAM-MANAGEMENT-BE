package com.swd.exe.teammanagement.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.swd.exe.teammanagement.dto.request.MajorRequest;
import com.swd.exe.teammanagement.dto.response.MajorResponse;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.mapper.MajorMapper;
import com.swd.exe.teammanagement.repository.MajorRepository;
import com.swd.exe.teammanagement.service.MajorService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class MajorServiceImpl implements MajorService {
    MajorRepository majorRepository;
    MajorMapper majorMapper;

    @Override
    @Transactional
    public MajorResponse createMajor(MajorRequest request) {
        if(majorRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new AppException(ErrorCode.MAJOR_EXISTED);
        }
        Major major = Major.builder()
                .name(request.getName())
                .active(true)
                .build();
        majorRepository.save(major);
        return majorMapper.toMajorResponse(major);
    }

    @Override
    public MajorResponse updateMajor(Long id, MajorRequest request) {
        Major major = majorRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.MAJOR_UNEXISTED));
            majorMapper.toUpdateMajor(major,request);
            majorRepository.save(major);
            return majorMapper.toMajorResponse(major);
    }

    @Override
    public String deleteMajor(Long id) {
        deactivateMajor(id);
        return "Major deleted successfully";
    }

    @Override
    public MajorResponse getMajorById(Long id) {
        return majorMapper.toMajorResponse(majorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_UNEXISTED)));
    }

    @Override
    public List<MajorResponse> getAllMajors() {
        return majorMapper.toMajorResponseList(majorRepository.findAll());
    }

    @Override
    public MajorResponse activateMajor(Long id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_UNEXISTED));
        major.setActive(true);
        majorRepository.save(major);
        return majorMapper.toMajorResponse(major);
    }

    @Override
    public MajorResponse deactivateMajor(Long id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_UNEXISTED));
        major.setActive(false);
        majorRepository.save(major);
        return majorMapper.toMajorResponse(major);
    }

    @Override
    public MajorResponse changeMajorActiveStatus(Long id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_UNEXISTED));
        major.setActive(!major.isActive());
        majorRepository.save(major);
        return majorMapper.toMajorResponse(major);
    }
}
