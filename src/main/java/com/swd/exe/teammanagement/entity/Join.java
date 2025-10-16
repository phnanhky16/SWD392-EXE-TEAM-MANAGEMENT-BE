package com.swd.exe.teammanagement.entity;

import com.swd.exe.teammanagement.enums.idea_join_post_score.JoinStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "`join`")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Join {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_group_id")
    Group toGroup;

    @Enumerated(EnumType.STRING)
    JoinStatus status;
    boolean active;
}
