// com.swd.exe.teammanagement.entity.ChatMessage
package com.swd.exe.teammanagement.entity;

import com.swd.exe.teammanagement.enums.chat.ChatRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    ChatSession session;

    @Enumerated(EnumType.STRING)
    ChatRole role;

    @Column(columnDefinition = "text")
    String content;

    LocalDateTime createdAt;
}
