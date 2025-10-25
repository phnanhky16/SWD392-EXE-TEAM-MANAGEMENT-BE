package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.response.UserStatusResponse;
import com.swd.exe.teammanagement.dto.response.TypingIndicatorResponse;

import java.util.List;
import java.util.Map;

public interface UserStatusService {
    void setUserOnline(Long userId);
    void setUserOffline(Long userId);
    void setUserStatus(Long userId, String status);
    UserStatusResponse getUserStatus(Long userId);
    List<UserStatusResponse> getOnlineUsersInGroup(Long groupId);
    void setTypingStatus(Long userId, Long groupId, boolean isTyping);
    List<TypingIndicatorResponse> getTypingUsers(Long groupId);
    Map<Long, String> getAllUserStatuses();
}
