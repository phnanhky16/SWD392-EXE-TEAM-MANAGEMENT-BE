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
public class ChatMessageResponse {
    private Long id;
    private String content;
    private Long groupId;
    private String groupTitle;
    private Long fromUserId;
    private String fromUserName;
    private String fromUserAvatar;
    private String messageType;
    private String replyToMessageId;
    private String replyToContent;
    private LocalDateTime createdAt;
    private boolean isEdited;
    private LocalDateTime editedAt;
}
