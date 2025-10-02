package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByStudentCode(String studentCode);
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u.email FROM User u WHERE u.email IN :emails")
    List<String> findAllEmailsByEmailIn(@Param("emails") List<String> emails);
}
