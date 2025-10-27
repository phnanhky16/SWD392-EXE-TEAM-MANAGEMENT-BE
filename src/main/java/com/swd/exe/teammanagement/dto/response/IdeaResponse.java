package com.swd.exe.teammanagement.dto.response;

import com.swd.exe.teammanagement.enums.idea_join_post_score.IdeaStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IdeaResponse {
    Long id;
    String title;
    String description;
    UserSummaryResponse author;
    UserSummaryResponse reviewer;
    GroupSummaryResponse group;
    IdeaStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
