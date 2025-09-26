package com.swd.exe.teammanagement.mapper;

import com.swd.exe.teammanagement.dto.request.MajorRequest;
import com.swd.exe.teammanagement.dto.response.MajorResponse;
import com.swd.exe.teammanagement.entity.Major;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MajorMapper {
    Major toMajor(MajorRequest request);
    MajorResponse toMajorResponse(Major major);
    void toUpdateMajor(@MappingTarget Major major, MajorRequest request);
    List<MajorResponse> toMajorResponseList(List<Major> majors);
}
