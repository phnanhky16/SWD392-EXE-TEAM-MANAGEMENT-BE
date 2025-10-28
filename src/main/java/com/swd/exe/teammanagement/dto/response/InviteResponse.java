package com.swd.exe.teammanagement.dto.response;

import com.swd.exe.teammanagement.enums.invite.InviteStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InviteResponse {
    Long id;
    GroupResponse group;
    UserResponse inviter;
    UserResponse invitee;
    InviteStatus status;
    LocalDateTime createdAt;
    LocalDateTime respondedAt;
}
