package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
	boolean existsByName(String name);

    Semester findByActive(Boolean active);
}
