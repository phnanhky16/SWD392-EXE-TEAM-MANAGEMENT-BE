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

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, GroupMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface IdeaMapper {

    /* ===== Create ===== */
    Idea toIdea(IdeaRequest request);

    /* ===== Read (entity -> DTO) ===== */
    @Mappings({
            @Mapping(target = "author",   source = "author",   qualifiedByName = "toUserSummary"),
            @Mapping(target = "reviewer", source = "reviewer", qualifiedByName = "toUserSummary"),
            @Mapping(target = "group",    source = "group",    qualifiedByName = "toGroupSummary")
            // các field primitive còn lại (id, title, description, status, createdAt, updatedAt) MapStruct tự map
    })
    IdeaResponse toIdeaResponse(Idea idea);

    @IterableMapping(qualifiedByName = "toIdeaResponse")
    List<IdeaResponse> toIdeaResponseList(List<Idea> ideas);

    @Named("toIdeaResponse")
    @Mappings({
            @Mapping(target = "author",   source = "author",   qualifiedByName = "toUserSummary"),
            @Mapping(target = "reviewer", source = "reviewer", qualifiedByName = "toUserSummary"),
            @Mapping(target = "group",    source = "group",    qualifiedByName = "toGroupSummary")
    })
    IdeaResponse toIdeaResponseNamed(Idea idea);

    /* ===== Update (partial) – bỏ qua field null ===== */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUpdateIdea(@MappingTarget Idea idea, IdeaRequest request);

    /* ===== Local converters (nếu chưa có trong UserMapper/GroupMapper) ===== */
    @Named("toUserSummary")
    default UserSummaryResponse toUserSummaryResponse(User u) {
        if (u == null) return null;
        return UserSummaryResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .role(u.getRole())
                .build();
    }

    @Named("toGroupSummary")
    default GroupSummaryResponse toGroupSummary(Group g) {
        if (g == null) return null;
        return GroupSummaryResponse.builder()
                .id(g.getId())
                .title(g.getTitle()) // nếu field entity là "name" thì đổi lại cho đúng
                .build();
    }
}
