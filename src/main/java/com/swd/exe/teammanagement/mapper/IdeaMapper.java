package com.swd.exe.teammanagement.mapper;

import com.swd.exe.teammanagement.dto.request.IdeaRequest;
import com.swd.exe.teammanagement.dto.response.GroupSummaryResponse;
import com.swd.exe.teammanagement.dto.response.IdeaResponse;
import com.swd.exe.teammanagement.dto.response.UserSummaryResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Idea;
import com.swd.exe.teammanagement.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, GroupMapper.class})
public interface IdeaMapper {
    Idea toIdea(IdeaRequest request);

    @Mapping(target = "author", source = "author")
    IdeaResponse toIdeaResponse(Idea idea);

    List<IdeaResponse> toIdeaResponseList(List<Idea> ideas);

    void toUpdateIdea(@MappingTarget Idea idea, IdeaRequest request);
    default UserSummaryResponse toUserSummaryResponse(User u) {
        if (u == null) return null;
        return UserSummaryResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .role(u.getRole())
                .build();
    }

    default GroupSummaryResponse toGroupSummary(Group g) {
        if (g == null) return null;
        return GroupSummaryResponse.builder()
                .id(g.getId())
                .title(g.getTitle())  // nếu entity là "name", đổi lại cho đúng
                .build();
    }
}

