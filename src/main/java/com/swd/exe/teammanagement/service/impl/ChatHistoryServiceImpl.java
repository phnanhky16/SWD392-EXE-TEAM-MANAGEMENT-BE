// com.swd.exe.teammanagement.service.impl.ChatHistoryServiceImpl
package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.entity.ChatMessage;
import com.swd.exe.teammanagement.entity.ChatSession;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.chat.ChatRole;
import com.swd.exe.teammanagement.repository.ChatMessageRepository;
import com.swd.exe.teammanagement.repository.ChatSessionRepository;
import com.swd.exe.teammanagement.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ChatHistoryServiceImpl implements ChatHistoryService {

    ChatSessionRepository chatSessionRepository;
    ChatMessageRepository chatMessageRepository;

    @Override
    public ChatSession getOrCreateActiveSession(User user) {
        return chatSessionRepository
                .findFirstByUserAndActiveTrueOrderByLastActivityAtDesc(user)
                .orElseGet(() -> {
                    ChatSession session = ChatSession.builder()
                            .user(user)
                            .title("Chatbot session")
                            .createdAt(LocalDateTime.now())
                            .lastActivityAt(LocalDateTime.now())
                            .active(true)
                            .build();
                    return chatSessionRepository.save(session);
                });
    }

    @Override
    public ChatMessage saveMessage(ChatSession session, ChatRole role, String content) {
        ChatMessage msg = ChatMessage.builder()
                .session(session)
                .role(role)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
        ChatMessage saved = chatMessageRepository.save(msg);

        session.setLastActivityAt(LocalDateTime.now());
        chatSessionRepository.save(session);

        return saved;
    }

    @Override
    public List<ChatMessage> getMessages(ChatSession session, int limit) {
        if (limit <= 0) return Collections.emptyList();
        List<ChatMessage> latestDesc = chatMessageRepository
                .findTop20BySessionOrderByCreatedAtDesc(session);
        Collections.reverse(latestDesc); // cho tăng dần thời gian
        return latestDesc;
    }
}
