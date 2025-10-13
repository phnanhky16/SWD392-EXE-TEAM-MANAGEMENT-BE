package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Join;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.idea_join_post_score.JoinStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinRepository extends JpaRepository<Join, Long> {
    void deleteJoinsByToGroup(Group toGroup);
    @Transactional
    void deleteJoinByFromUser(User fromUser);

    List<Join> findByToGroupAndStatus(Group toGroup, JoinStatus status);

    List<Join> findByFromUserAndStatus(User fromUser, JoinStatus status);
}
