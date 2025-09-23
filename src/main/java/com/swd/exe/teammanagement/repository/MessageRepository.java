package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
