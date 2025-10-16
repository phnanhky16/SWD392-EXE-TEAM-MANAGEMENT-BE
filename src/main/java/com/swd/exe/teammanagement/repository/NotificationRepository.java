package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Notification;
import com.swd.exe.teammanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByReceiverOrderByCreatedAtDesc(User receiver);
}
