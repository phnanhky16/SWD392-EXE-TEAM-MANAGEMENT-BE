package com.swd.exe.teammanagement.dto.response;

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
    Long leaderId;
    String leaderName;
    Long groupId;
    String groupName;
    String description;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Long approvedById;
    String rejectionReason;
    LocalDateTime approvedAt;
}
