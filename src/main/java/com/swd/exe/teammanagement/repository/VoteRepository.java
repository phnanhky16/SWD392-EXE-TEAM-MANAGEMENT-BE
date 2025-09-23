package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
