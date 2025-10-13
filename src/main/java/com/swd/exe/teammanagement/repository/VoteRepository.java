package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Vote;
import com.swd.exe.teammanagement.enums.vote.VoteStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Transactional
    void deleteVotesByGroup(Group group);

    List<Vote> findByStatus(VoteStatus status);

    List<Vote> findByGroup(Group group);
}
