package com.swd.exe.teammanagement.entity;

import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


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

    @ManyToOne
    @JoinColumn(name = "leader_id")
    User leader;

    @Enumerated(EnumType.STRING)
    GroupType type;

    @Enumerated(EnumType.STRING)
    GroupStatus status;

    @ManyToOne
    @JoinColumn(name = "checkpoint_lecture_id")
    User checkpointLecture;

}

