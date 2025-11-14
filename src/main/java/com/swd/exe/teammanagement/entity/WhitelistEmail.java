package com.swd.exe.teammanagement.entity;

import com.swd.exe.teammanagement.enums.user.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "whitelist_emails", indexes = {
    @Index(name = "idx_whitelist_email", columnList = "email"),
    @Index(name = "idx_whitelist_active", columnList = "is_active"),
    @Index(name = "idx_whitelist_semester", columnList = "semester_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WhitelistEmail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @Column(nullable = false, unique = true)
    String email;
    
    @Column(name = "full_name")
    String fullName;
    
    @Column(name = "student_code")
    String studentCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    UserRole role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    Semester semester;
    
    @Column(name = "is_active")
    @Builder.Default
    Boolean isActive = true;
    
    @Column(name = "created_at")
    LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }
}
