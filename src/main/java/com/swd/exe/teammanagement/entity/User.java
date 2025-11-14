package com.swd.exe.teammanagement.entity;

import java.util.ArrayList;
import java.util.List;

import com.swd.exe.teammanagement.enums.user.UserRole;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @ManyToMany
    @JoinTable(
            name = "user_semesters",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "semester_id")
    )
    @Builder.Default
    List<Semester> semesters = new ArrayList<>();

}

