package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.request.ChatMessageRequest;
import com.swd.exe.teammanagement.dto.response.ChatMessageResponse;
import com.swd.exe.teammanagement.dto.response.PagingResponse;
import com.swd.exe.teammanagement.entity.Message;

import java.util.List;

public interface MessageService {
    ChatMessageResponse sendMessage(ChatMessageRequest request, Long userId);
    List<ChatMessageResponse> getGroupMessages(Long groupId, Long userId, int page, int size);
    ChatMessageResponse editMessage(Long messageId, String newContent, Long userId);
    void deleteMessage(Long messageId, Long userId);
    List<ChatMessageResponse> searchMessages(Long groupId, Long userId, String keyword);
    ChatMessageResponse getMessageById(Long messageId);
}
