package com.swd.exe.teammanagement.entity;

import com.swd.exe.teammanagement.enums.idea_join_post_score.IdeaSource;
import com.swd.exe.teammanagement.enums.idea_join_post_score.IdeaStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ideas",
        indexes = {
                @Index(name = "idx_ideas_group", columnList = "group_id"),
                @Index(name = "idx_ideas_author", columnList = "author_id"),
                @Index(name = "idx_ideas_source", columnList = "source"),
                @Index(name = "idx_ideas_status", columnList = "status"),
                @Index(name = "idx_ideas_combined_key", columnList = "combined_key")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Idea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 255)
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    IdeaSource source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    IdeaStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    Group group; // nullable

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    User reviewer; // nullable

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.status == null) this.status = IdeaStatus.DRAFT;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
