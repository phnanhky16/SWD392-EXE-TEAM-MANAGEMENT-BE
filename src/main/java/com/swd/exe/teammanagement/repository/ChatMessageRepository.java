package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.ChatMessage;
import com.swd.exe.teammanagement.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySessionOrderByCreatedAtAsc(ChatSession session);

    List<ChatMessage> findTop20BySessionOrderByCreatedAtDesc(ChatSession session);
}
