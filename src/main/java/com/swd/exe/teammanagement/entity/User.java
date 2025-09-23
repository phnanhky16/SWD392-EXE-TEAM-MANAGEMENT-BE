package com.swd.exe.teammanagement.entity;

import com.swd.exe.teammanagement.enums.user.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

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

    @Column(unique = true, nullable = false)
    String studentCode;

    String fullName;

    @Column(unique = true, nullable = false)
    String email;

    String password;

    String cvUrl;

    @ManyToOne
    @JoinColumn(name = "major_code")
    Major major;

    @Enumerated(EnumType.STRING)
    UserRole role;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

