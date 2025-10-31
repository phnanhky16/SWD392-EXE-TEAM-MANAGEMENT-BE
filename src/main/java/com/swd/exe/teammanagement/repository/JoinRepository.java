package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Join;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.idea_join_post_score.JoinStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JoinRepository extends JpaRepository<Join, Long> {
    void deleteJoinsByToGroup(Group toGroup);
    
    @org.springframework.data.jpa.repository.Query("UPDATE Join j SET j.active = false WHERE j.toGroup = :toGroup")
    @org.springframework.data.jpa.repository.Modifying
    @Transactional
    void deactivateJoinsByToGroup(@org.springframework.data.repository.query.Param("toGroup") Group toGroup);
    
    @Transactional
    void deleteJoinByFromUser(User fromUser);

    @org.springframework.data.jpa.repository.Query("UPDATE Join j SET j.active = false WHERE j.fromUser = :fromUser")
    @org.springframework.data.jpa.repository.Modifying
    @Transactional
    void deactivateJoinsByFromUser(@org.springframework.data.repository.query.Param("fromUser") User fromUser);

    List<Join> findByToGroupAndStatusAndActiveTrue(Group toGroup, JoinStatus status);

    List<Join> findByFromUserAndStatusAndActiveTrue(User fromUser, JoinStatus status);

    boolean existsByFromUserAndToGroupAndActiveTrue(User fromUser, Group toGroup);

    Optional<Join> findJoinByFromUserAndToGroupAndActiveTrue(User fromUser, Group toGroup);
    
    List<Join> findByActiveTrue();
    
    List<Join> findByActiveFalse();

    boolean countJoinByFromUser(User fromUser);

    double countJoinsByFromUser(User fromUser);
}
