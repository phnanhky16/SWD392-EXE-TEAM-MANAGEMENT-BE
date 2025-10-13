package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {

    @Query("SELECT gm.user FROM GroupMember gm WHERE gm.group = :group AND gm.membershipRole = 'LEADER'")
    Optional<User> findLeaderByGroup(@Param("group") Group group);


    List<Group> findGroupsByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);
    List<Group> findGroupsByStatusInAndCreatedAtBetween(Collection<GroupStatus> statuses, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);
}
