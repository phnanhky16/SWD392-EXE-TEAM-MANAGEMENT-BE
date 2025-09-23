package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Idea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdeaRepository extends JpaRepository<Idea, Long> {
}
