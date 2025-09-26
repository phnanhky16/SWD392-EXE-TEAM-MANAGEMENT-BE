package com.swd.exe.teammanagement.entity;

import com.swd.exe.teammanagement.enums.user.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String studentCode;

    String fullName;

    String email;

    String cvUrl;

    String avatarUrl;

    @ManyToOne
    @JoinColumn(name = "major_code")
    Major major;

    @Enumerated(EnumType.STRING)
    UserRole role;

    Boolean isActive;

}

