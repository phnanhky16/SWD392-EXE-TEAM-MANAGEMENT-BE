package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.response.TypingIndicatorResponse;
import com.swd.exe.teammanagement.dto.response.UserStatusResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.repository.GroupMemberRepository;
import com.swd.exe.teammanagement.repository.GroupRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class UserStatusServiceImpl implements UserStatusService {

    UserRepository userRepository;
    GroupRepository groupRepository;
    GroupMemberRepository groupMemberRepository;
    SimpMessagingTemplate messagingTemplate;

    // In-memory storage for user statuses
    private final Map<Long, String> userStatuses = new ConcurrentHashMap<>();
    private final Map<Long, LocalDateTime> lastSeenTimes = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> typingStatuses = new ConcurrentHashMap<>();
    private final Map<Long, Long> currentGroupIds = new ConcurrentHashMap<>();

    @Override
    public void setUserOnline(Long userId) {
        userStatuses.put(userId, "ONLINE");
        lastSeenTimes.put(userId, LocalDateTime.now());
        
        // Notify all groups that this user is online
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            List<Group> userGroups = groupMemberRepository.findByUserAndActiveTrue(user).stream()
                    .map(member -> member.getGroup())
                    .collect(Collectors.toList());
            for (Group group : userGroups) {
                messagingTemplate.convertAndSend("/topic/group." + group.getId() + ".status", 
                    UserStatusResponse.builder()
                        .userId(userId)
                        .userName(user.getFullName())
                        .userAvatar(user.getAvatarUrl())
                        .status("ONLINE")
                        .lastSeen(LocalDateTime.now())
                        .build());
            }
        }
    }

    @Override
    public void setUserOffline(Long userId) {
        userStatuses.put(userId, "OFFLINE");
        lastSeenTimes.put(userId, LocalDateTime.now());
        typingStatuses.remove(userId);
        currentGroupIds.remove(userId);
        
        // Notify all groups that this user is offline
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            List<Group> userGroups = groupMemberRepository.findByUserAndActiveTrue(user).stream()
                    .map(member -> member.getGroup())
                    .collect(Collectors.toList());
            for (Group group : userGroups) {
                messagingTemplate.convertAndSend("/topic/group." + group.getId() + ".status", 
                    UserStatusResponse.builder()
                        .userId(userId)
                        .userName(user.getFullName())
                        .userAvatar(user.getAvatarUrl())
                        .status("OFFLINE")
                        .lastSeen(LocalDateTime.now())
                        .build());
            }
        }
    }

    @Override
    public void setUserStatus(Long userId, String status) {
        userStatuses.put(userId, status);
        lastSeenTimes.put(userId, LocalDateTime.now());
        
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            List<Group> userGroups = groupMemberRepository.findByUserAndActiveTrue(user).stream()
                    .map(member -> member.getGroup())
                    .collect(Collectors.toList());
            for (Group group : userGroups) {
                messagingTemplate.convertAndSend("/topic/group." + group.getId() + ".status", 
                    UserStatusResponse.builder()
                        .userId(userId)
                        .userName(user.getFullName())
                        .userAvatar(user.getAvatarUrl())
                        .status(status)
                        .lastSeen(LocalDateTime.now())
                        .build());
            }
        }
    }

    @Override
    public UserStatusResponse getUserStatus(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        String status = userStatuses.getOrDefault(userId, "OFFLINE");
        LocalDateTime lastSeen = lastSeenTimes.getOrDefault(userId, LocalDateTime.now());
        boolean isTyping = typingStatuses.getOrDefault(userId, false);
        Long currentGroupId = currentGroupIds.get(userId);

        return UserStatusResponse.builder()
                .userId(userId)
                .userName(user.getFullName())
                .userAvatar(user.getAvatarUrl())
                .status(status)
                .lastSeen(lastSeen)
                .isTyping(isTyping)
                .currentGroupId(currentGroupId != null ? currentGroupId.toString() : null)
                .build();
    }

    @Override
    public List<UserStatusResponse> getOnlineUsersInGroup(Long groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) return List.of();

        return groupMemberRepository.findByGroupAndActiveTrue(group).stream()
                .map(member -> getUserStatus(member.getUser().getId()))
                .filter(status -> status != null && "ONLINE".equals(status.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public void setTypingStatus(Long userId, Long groupId, boolean isTyping) {
        typingStatuses.put(userId, isTyping);
        currentGroupIds.put(userId, groupId);

        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            TypingIndicatorResponse indicator = TypingIndicatorResponse.builder()
                    .userId(userId)
                    .userName(user.getFullName())
                    .groupId(groupId)
                    .isTyping(isTyping)
                    .build();

            messagingTemplate.convertAndSend("/topic/group." + groupId + ".typing", indicator);
        }
    }

    @Override
    public List<TypingIndicatorResponse> getTypingUsers(Long groupId) {
        return typingStatuses.entrySet().stream()
                .filter(entry -> entry.getValue() && currentGroupIds.get(entry.getKey()) != null && 
                        currentGroupIds.get(entry.getKey()).equals(groupId))
                .map(entry -> {
                    User user = userRepository.findById(entry.getKey()).orElse(null);
                    if (user == null) return null;
                    
                    return TypingIndicatorResponse.builder()
                            .userId(entry.getKey())
                            .userName(user.getFullName())
                            .groupId(groupId)
                            .isTyping(true)
                            .build();
                })
                .filter(indicator -> indicator != null)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, String> getAllUserStatuses() {
        return Map.copyOf(userStatuses);
    }
}
