package com.swd.exe.teammanagement.entity;

import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "`groups`")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Enumerated(EnumType.STRING)
    GroupType type;

    @Enumerated(EnumType.STRING)
    GroupStatus status;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    Semester semester;

    LocalDateTime createdAt;
    boolean active = true;
}

