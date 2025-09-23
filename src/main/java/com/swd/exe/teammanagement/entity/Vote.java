package com.swd.exe.teammanagement.entity;

import com.swd.exe.teammanagement.enums.vote.VoteStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "votes")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    Group group;

    @ManyToOne
    @JoinColumn(name = "created_by")
    User createdBy;

    String topic;

    @Enumerated(EnumType.STRING)
    VoteStatus status;

    LocalDateTime createdAt;
    LocalDateTime closedAt;
}

