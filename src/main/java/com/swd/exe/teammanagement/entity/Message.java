package com.swd.exe.teammanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    Group group;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    User fromUser;

    @Column(columnDefinition = "TEXT")
    String messageText;
    
    @Column(name = "message_type")
    String messageType; // "TEXT", "IMAGE", "FILE"
    
    @ManyToOne
    @JoinColumn(name = "reply_to_message_id")
    Message replyToMessage;
    
    @Column(name = "is_edited")
    boolean isEdited;
    
    @Column(name = "edited_at")
    LocalDateTime editedAt;
    
    @Column(name = "created_at")
    LocalDateTime createdAt;
    
    boolean active;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}