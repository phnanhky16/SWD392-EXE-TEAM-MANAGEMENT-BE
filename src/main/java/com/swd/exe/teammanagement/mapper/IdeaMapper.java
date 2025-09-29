package com.swd.exe.teammanagement.mapper;

import com.swd.exe.teammanagement.dto.request.IdeaRequest;
import com.swd.exe.teammanagement.dto.response.IdeaResponse;
import com.swd.exe.teammanagement.entity.Idea;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IdeaMapper {

    Idea toIdea(IdeaRequest request);

    IdeaResponse toIdeaResponse(Idea idea);

    List<IdeaResponse> toIdeaResponseList(List<Idea> ideas);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUpdateIdea(@MappingTarget Idea target, IdeaRequest request);
}
