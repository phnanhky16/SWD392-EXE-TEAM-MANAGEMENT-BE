package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.entity.Notification;

import java.util.List;

public interface NotificationService {
    void markAsRead(Long notificationId, Long userId);
    List<Notification> getMyNotifications(String email);
}
