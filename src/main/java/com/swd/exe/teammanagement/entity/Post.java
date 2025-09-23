package com.swd.exe.teammanagement.entity;

import com.swd.exe.teammanagement.enums.idea_join_post.PostType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = true)
    Group group;

    @Enumerated(EnumType.STRING)
    PostType type;

    @Column(columnDefinition = "TEXT")
    String content;

    LocalDateTime createdAt;
}

