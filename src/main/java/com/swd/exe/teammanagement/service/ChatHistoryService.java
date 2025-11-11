package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.entity.ChatMessage;
import com.swd.exe.teammanagement.entity.ChatSession;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.chat.ChatRole;

import java.util.List;

public interface ChatHistoryService {

    ChatSession getOrCreateActiveSession(User user);

    ChatMessage saveMessage(ChatSession session, ChatRole role, String content);

    List<ChatMessage> getMessages(ChatSession session, int limit);
}
