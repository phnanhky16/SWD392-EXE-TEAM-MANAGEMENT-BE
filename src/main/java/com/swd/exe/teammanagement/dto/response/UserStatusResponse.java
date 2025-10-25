package com.swd.exe.teammanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusResponse {
    private Long userId;
    private String userName;
    private String userAvatar;
    private String status; // "ONLINE", "OFFLINE", "AWAY", "BUSY"
    private LocalDateTime lastSeen;
    private boolean isTyping;
    private String currentGroupId;
}
