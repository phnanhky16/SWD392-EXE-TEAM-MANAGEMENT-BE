package com.swd.exe.teammanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    private String content;
    private Long groupId;
    private String messageType; // "TEXT", "IMAGE", "FILE"
    private String replyToMessageId; // For replies
}
