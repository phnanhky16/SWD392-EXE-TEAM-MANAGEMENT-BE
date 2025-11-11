package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.ChatSession;
import com.swd.exe.teammanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Optional<ChatSession> findFirstByUserAndActiveTrueOrderByLastActivityAtDesc(User user);

    List<ChatSession> findByUserOrderByLastActivityAtDesc(User user);
}
