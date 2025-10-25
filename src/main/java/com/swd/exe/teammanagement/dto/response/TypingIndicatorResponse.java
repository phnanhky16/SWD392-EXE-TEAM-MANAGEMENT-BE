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
public class TypingIndicatorResponse {
    private Long userId;
    private String userName;
    private Long groupId;
    private boolean isTyping;
    private LocalDateTime timestamp;
}
