package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.request.ChatMessageRequest;
import com.swd.exe.teammanagement.dto.response.ChatMessageResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Message;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.GroupMemberRepository;
import com.swd.exe.teammanagement.repository.GroupRepository;
import com.swd.exe.teammanagement.repository.MessageRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class MessageServiceImpl implements MessageService {

    MessageRepository messageRepository;
    UserRepository userRepository;
    GroupRepository groupRepository;
    GroupMemberRepository groupMemberRepository;

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(ChatMessageRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));

        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

        // Check if user is member of the group - CHAT CHỈ DÀNH CHO MEMBERS TRONG NHÓM
        boolean isMember = groupMemberRepository.existsByGroupAndUserAndActiveTrue(group, user);
        if (!isMember) {
            throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
        }

        Message replyToMessage = null;
        if (request.getReplyToMessageId() != null) {
            replyToMessage = messageRepository.findById(Long.parseLong(request.getReplyToMessageId()))
                    .orElseThrow(() -> new AppException(ErrorCode.POST_UNEXISTED));
        }

        Message message = Message.builder()
                .group(group)
                .fromUser(user)
                .messageText(request.getContent())
                .messageType(request.getMessageType() != null ? request.getMessageType() : "TEXT")
                .replyToMessage(replyToMessage)
                .active(true)
                .build();

        Message savedMessage = messageRepository.save(message);
        return convertToResponse(savedMessage);
    }

    @Override
    public List<ChatMessageResponse> getGroupMessages(Long groupId, Long userId, int page, int size) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));

        // CHAT CHỈ DÀNH CHO MEMBERS TRONG NHÓM
        boolean isMember = groupMemberRepository.existsByGroupAndUserAndActiveTrue(group, user);
        if (!isMember) {
            throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Message> messages = messageRepository.findByGroupAndActiveTrueOrderByCreatedAtDesc(group, pageable);

        return messages.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatMessageResponse editMessage(Long messageId, String newContent, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_UNEXISTED));

        if (!message.getFromUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.DOES_NOT_DELETE_OTHER_USER_POST);
        }

        message.setMessageText(newContent);
        message.setEdited(true);
        message.setEditedAt(LocalDateTime.now());

        Message updatedMessage = messageRepository.save(message);
        return convertToResponse(updatedMessage);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_UNEXISTED));

        if (!message.getFromUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.DOES_NOT_DELETE_OTHER_USER_POST);
        }

        message.setActive(false);
        messageRepository.save(message);
    }

    @Override
    public List<ChatMessageResponse> searchMessages(Long groupId, Long userId, String keyword) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));

        // CHAT CHỈ DÀNH CHO MEMBERS TRONG NHÓM
        boolean isMember = groupMemberRepository.existsByGroupAndUserAndActiveTrue(group, user);
        if (!isMember) {
            throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
        }

        List<Message> messages = messageRepository.findByGroupAndActiveTrueAndMessageTextContainingIgnoreCaseOrderByCreatedAtDesc(
                group, keyword);

        return messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChatMessageResponse getMessageById(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_UNEXISTED));

        return convertToResponse(message);
    }

    private ChatMessageResponse convertToResponse(Message message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .content(message.getMessageText())
                .groupId(message.getGroup().getId())
                .groupTitle(message.getGroup().getTitle())
                .fromUserId(message.getFromUser().getId())
                .fromUserName(message.getFromUser().getFullName())
                .fromUserAvatar(message.getFromUser().getAvatarUrl())
                .messageType(message.getMessageType())
                .replyToMessageId(message.getReplyToMessage() != null ? message.getReplyToMessage().getId().toString() : null)
                .replyToContent(message.getReplyToMessage() != null ? message.getReplyToMessage().getMessageText() : null)
                .createdAt(message.getCreatedAt())
                .isEdited(message.isEdited())
                .editedAt(message.getEditedAt())
                .build();
    }
}
