package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Semester;
import com.swd.exe.teammanagement.entity.WhitelistEmail;
import com.swd.exe.teammanagement.enums.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WhitelistEmailRepository extends JpaRepository<WhitelistEmail, Long> {
    
    Optional<WhitelistEmail> findByEmailAndIsActiveTrue(String email);
    
    boolean existsByEmailAndIsActiveTrue(String email);
    
    List<WhitelistEmail> findBySemesterAndIsActiveTrue(Semester semester);
    
    List<WhitelistEmail> findBySemesterAndRoleAndIsActiveTrue(Semester semester, UserRole role);
    
    @Modifying
    @Query("DELETE FROM WhitelistEmail w WHERE w.email = :email")
    void deleteByEmail(@Param("email") String email);
}
