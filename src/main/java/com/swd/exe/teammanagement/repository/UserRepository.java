package com.swd.exe.teammanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swd.exe.teammanagement.entity.Semester;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.user.UserRole;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByStudentCode(String studentCode);
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u.email FROM User u WHERE u.email IN :emails")
    List<String> findAllEmailsByEmailIn(@Param("emails") List<String> emails);

    @Override
    @EntityGraph(attributePaths = "major")
    Page<User> findAll(Specification<User> spec, Pageable pageable);

    List<User> findByRole(UserRole role);
    @Query("SELECT u FROM User u WHERE u.id NOT IN (SELECT gm.user.id FROM GroupMember gm)")
    List<User> findUsersWithoutGroup();
    
    // Query methods for users by semester
    @Query("SELECT u FROM User u JOIN u.semesters s WHERE s = :semester AND u.role = :role AND u.isActive = true")
    List<User> findByRoleAndSemestersContainingAndIsActiveTrue(@Param("role") UserRole role, @Param("semester") Semester semester);
    
    @Query("SELECT u FROM User u JOIN u.semesters s WHERE s.id = :semesterId AND u.role = 'STUDENT' AND u.isActive = true")
    List<User> findStudentsBySemesterId(@Param("semesterId") Long semesterId);
    
    @Query("SELECT u FROM User u JOIN u.semesters s WHERE s.id = :semesterId AND u.role = 'LECTURER' AND u.isActive = true")
    List<User> findTeachersBySemesterId(@Param("semesterId") Long semesterId);
    
    @Query("SELECT u FROM User u JOIN u.semesters s WHERE s.id = :semesterId AND u.role = 'MODERATOR' AND u.isActive = true")
    List<User> findModeratorsBySemesterId(@Param("semesterId") Long semesterId);
    
    // Query to deactivate users of a specific semester (except ADMIN users and specific admin email)
    @Modifying
    @Query("UPDATE User u SET u.isActive = false WHERE u IN (SELECT u2 FROM User u2 JOIN u2.semesters s WHERE s.id = :semesterId) AND u.role != 'ADMIN'")
    void deactivateUsersBySemesterId(@Param("semesterId") Long semesterId);
}
