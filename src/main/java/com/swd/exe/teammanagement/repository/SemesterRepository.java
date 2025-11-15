package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
	boolean existsByNameAndActiveTrue(String name);
	
	Optional<Semester> findByName(String name);

    Semester findByActiveTrue();
    
    List<Semester> findByActiveFalse();

    boolean existsByActive(Boolean active);
}
