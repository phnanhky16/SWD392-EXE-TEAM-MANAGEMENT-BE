package com.swd.exe.teammanagement.service.impl;

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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class MajorServiceImpl implements MajorService {
    MajorRepository majorRepository;
    MajorMapper majorMapper;

    @Override
    @Transactional
    public MajorResponse createMajor(MajorRequest request) {
        if(majorRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.MAJOR_EXISTED);
        }
        Major major = Major.builder()
                .name(request.getName())
                .build();
        majorRepository.save(major);
        System.out.println(">>> Saved major with ID: " + major.getId());
        return MajorResponse.builder()
                .id(major.getId())
                .name(major.getName())
                .build();
    }

    @Override
    public MajorResponse updateMajor(Long id, MajorRequest request) {
        Major major = majorRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.MAJOR_UNEXISTED));
            majorMapper.toUpdateMajor(major,request);
            majorRepository.save(major);
            return majorMapper.toMajorResponse(major);
    }

    @Override
    public Void deleteMajor(Long id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_UNEXISTED));
        majorRepository.delete(major);
        return null;
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
}
