package com.swd.exe.teammanagement.dto.response;

import java.time.LocalDateTime;

import com.swd.exe.teammanagement.enums.idea_join_post_score.IdeaStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

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
    Boolean active;
}
