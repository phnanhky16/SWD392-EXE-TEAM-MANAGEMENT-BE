package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByLeader(User leader);

    void deleteGroupByLeader(User leader);

    List<Group> findGroupsByStatusAndType(GroupStatus status, GroupType type);

    List<Group> findGroupsByStatusAndTypeAndCreatedAtBetween(GroupStatus status, GroupType type, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);
}
