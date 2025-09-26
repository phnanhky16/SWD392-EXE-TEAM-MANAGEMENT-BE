package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MajorRepository extends JpaRepository<Major,String> {
    boolean existsByCode(String code);
}
