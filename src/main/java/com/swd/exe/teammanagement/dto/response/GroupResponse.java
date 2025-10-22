package com.swd.exe.teammanagement.dto.response;

import com.swd.exe.teammanagement.entity.Semester;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupResponse {
    Long id;
    String title;
    String description;
    Semester semester;
    GroupType type;
    GroupStatus status;
    LocalDateTime createdAt;
    boolean active;

}
