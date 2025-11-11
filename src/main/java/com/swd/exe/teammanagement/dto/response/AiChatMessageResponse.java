package com.swd.exe.teammanagement.dto.response;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AiChatMessageResponse {
        String role;          // USER / ASSISTANT / SYSTEM
        String content;
        LocalDateTime createdAt;
    }