package com.swd.exe.teammanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swd.exe.teammanagement.entity.Notification;
import com.swd.exe.teammanagement.entity.User;

import jakarta.transaction.Transactional;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByReceiverOrderByCreatedAtDesc(User receiver);
}
