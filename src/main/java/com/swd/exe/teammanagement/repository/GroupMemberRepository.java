package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    boolean existsByGroupIdAndUserIdAndMembershipRole(Long groupId, Long userId, MembershipRole role);
    Optional<GroupMember> findByUser(User user);
    boolean existsByUser(User user);
    int countByGroup(Group group);

    List<User> findUsersByGroup(Group group);

    void deleteByUser(User user);

    void deleteGroupMemberByUser(User user);

    @Query("SELECT gm.user FROM GroupMember gm WHERE gm.group.id = :groupId")
    List<User> findUsersByGroupId(@Param("groupId") Long groupId);

    int countByGroupId(Long groupId);

    @Query("SELECT gm.user.major FROM GroupMember gm WHERE gm.group.id = :groupId")
    List<Major> findMajorsByGroupId(@Param("groupId") Long groupId);

    List<GroupMember> findByGroup(Group group);

    Optional<GroupMember> findByGroupAndMembershipRole(Group group, MembershipRole membershipRole);
    
    boolean existsByGroupAndUserAndActiveTrue(Group group, User user);
}
