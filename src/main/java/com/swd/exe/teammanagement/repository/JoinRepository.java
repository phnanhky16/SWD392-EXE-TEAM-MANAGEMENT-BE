package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Join;
import com.swd.exe.teammanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinRepository extends JpaRepository<Join, Long> {
    void deleteJoinsByToGroup(Group toGroup);

    void deleteJoinByFromUser(User fromUser);
}
