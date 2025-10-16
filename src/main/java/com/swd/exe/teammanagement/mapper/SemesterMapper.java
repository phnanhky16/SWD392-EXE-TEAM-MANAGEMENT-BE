package com.swd.exe.teammanagement.mapper;

import com.swd.exe.teammanagement.dto.request.SemesterRequest;
import com.swd.exe.teammanagement.dto.response.SemesterResponse;
import com.swd.exe.teammanagement.entity.Semester;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SemesterMapper {
    SemesterResponse toSemesterResponse(Semester semester);
    Semester toSemester(SemesterRequest request);
    List<SemesterResponse> toSemesterResponseList(List<Semester> semesters);
    void toUpdateSemester(@MappingTarget Semester semester, SemesterRequest request);
}
