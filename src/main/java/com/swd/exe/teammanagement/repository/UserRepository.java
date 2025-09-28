package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByStudentCode(String studentCode);
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
