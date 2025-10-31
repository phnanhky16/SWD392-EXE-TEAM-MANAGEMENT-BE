package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.entity.Vote;
import com.swd.exe.teammanagement.enums.vote.VoteStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Transactional
    void deleteVotesByGroup(Group group);

    @org.springframework.data.jpa.repository.Query("UPDATE Vote v SET v.active = false WHERE v.group = :group")
    @org.springframework.data.jpa.repository.Modifying
    @Transactional
    void deactivateVotesByGroup(@org.springframework.data.repository.query.Param("group") Group group);

    List<Vote> findByStatusAndActiveTrue(VoteStatus status);

    List<Vote> findByGroupAndActiveTrue(Group group);
    
    List<Vote> findByActiveTrue();
    
    List<Vote> findByActiveFalse();
    Vote findByGroupAndTargetUserAndStatus(Group group, User targetUser, VoteStatus status);

    List<Vote> findByTargetUserAndStatus(User targetUser, VoteStatus status);
}
