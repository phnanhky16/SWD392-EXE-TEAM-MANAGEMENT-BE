package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.entity.Vote;
import com.swd.exe.teammanagement.entity.VoteChoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteChoiceRepository extends JpaRepository<VoteChoice, Long> {
    List<VoteChoice> findByVote(Vote vote);

    Optional<Object> findByVoteAndUser(Vote vote, User user);
}
