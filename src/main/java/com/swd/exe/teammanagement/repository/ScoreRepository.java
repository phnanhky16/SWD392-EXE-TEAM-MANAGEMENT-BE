package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score, Long> {
}
