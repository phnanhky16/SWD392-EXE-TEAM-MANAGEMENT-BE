package com.swd.exe.teammanagement.entity;

import com.swd.exe.teammanagement.enums.idea_join_post_score.SubjectCode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "scores")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    SubjectCode subjectCode;

    @ManyToOne
    @JoinColumn(name = "group_id")
    Group group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    Integer scoreValue;

    LocalDateTime updatedAt;
}
