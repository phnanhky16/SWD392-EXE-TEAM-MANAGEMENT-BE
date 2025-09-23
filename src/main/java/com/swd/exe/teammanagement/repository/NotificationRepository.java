package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
}
