package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
