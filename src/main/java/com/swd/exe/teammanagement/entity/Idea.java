    package com.swd.exe.teammanagement.entity;

    import com.swd.exe.teammanagement.enums.idea_join_post_score.IdeaStatus;
    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "ideas",
            indexes = {
                    @Index(name = "idx_idea_group", columnList = "group_id"),
                    @Index(name = "idx_idea_user",  columnList = "user_id"),
                    @Index(name = "idx_idea_status", columnList = "status")
            })
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public class Idea {

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "group_id", nullable = false)
        Group group;

        // Leader của ý tưởng
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        User user;

        String title;

        @Column(columnDefinition = "TEXT")
        String description;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        IdeaStatus status;

        // --- Thông tin duyệt ---
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "approved_by_id")
        User approvedBy;                 // giáo viên/ admin duyệt hoặc từ chối

        LocalDateTime approvedAt;        // thời điểm duyệt/ từ chối
        @Column(length = 500)
        String rejectionReason;          // lý do reject (nếu có)

        LocalDateTime createdAt;
        LocalDateTime updatedAt;

        @PrePersist void prePersist() { createdAt = LocalDateTime.now(); }
        @PreUpdate  void preUpdate()  { updatedAt = LocalDateTime.now(); }
    }
