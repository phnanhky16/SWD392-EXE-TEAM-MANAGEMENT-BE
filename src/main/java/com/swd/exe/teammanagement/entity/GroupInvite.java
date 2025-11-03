package com.swd.exe.teammanagement.entity;

import com.swd.exe.teammanagement.enums.invite.InviteStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_invites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id")
    User inviter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id")
    User invitee;

    @Enumerated(EnumType.STRING)
    InviteStatus status;

    LocalDateTime createdAt;

    LocalDateTime respondedAt;
    
    @Column(name = "active")
    @Builder.Default
    Boolean active = true;
}
