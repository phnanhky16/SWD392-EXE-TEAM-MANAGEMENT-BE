package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MajorRepository extends JpaRepository<Major,Long> {
    boolean existsByNameAndActiveTrue(String name);
    
    List<Major> findByActiveTrue();
    
    List<Major> findByActiveFalse();
}
