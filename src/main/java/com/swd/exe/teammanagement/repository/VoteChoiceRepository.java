package com.swd.exe.teammanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.entity.Vote;
import com.swd.exe.teammanagement.entity.VoteChoice;

import jakarta.transaction.Transactional;

public interface VoteChoiceRepository extends JpaRepository<VoteChoice, Long> {
    List<VoteChoice> findByVoteAndActiveTrue(Vote vote);

    Optional<Object> findByVoteAndUserAndActiveTrue(Vote vote, User user);
    
    List<VoteChoice> findByActiveTrue();
    
    List<VoteChoice> findByActiveFalse();

    List<VoteChoice> findVoteChoicesByVote(Vote vote);

    boolean existsByVoteAndUserAndActive(Vote vote, User user, boolean active);
    
    @Modifying
    @Transactional
    @Query("UPDATE VoteChoice vc SET vc.active = false WHERE vc.vote.group.semester.id = :semesterId")
    void deactivateVoteChoicesBySemester(@Param("semesterId") Long semesterId);
}
