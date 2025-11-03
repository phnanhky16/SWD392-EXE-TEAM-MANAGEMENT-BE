package com.swd.exe.teammanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import org.springframework.transaction.annotation.Transactional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    boolean existsByGroupIdAndUserIdAndMembershipRoleAndActiveTrue(Long groupId, Long userId, MembershipRole role);
    Optional<GroupMember> findByUserAndActiveTrue(User user);
    boolean existsByUserAndActiveTrue(User user);
    int countByGroupAndActiveTrue(Group group);

    @Query("SELECT gm.user FROM GroupMember gm WHERE gm.group = :group AND gm.active = true")
    List<User> findUsersByGroup(@Param("group") Group group);

    void deleteByUser(User user);

    void deleteGroupMemberByUser(User user);

    @Query("SELECT gm.user FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.active = true")
    List<User> findUsersByGroupId(@Param("groupId") Long groupId);

    int countByGroupIdAndActiveTrue(Long groupId);

    @Query("SELECT gm.user.major FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.active = true")
    List<Major> findMajorsByGroupId(@Param("groupId") Long groupId);

    List<GroupMember> findByGroupAndActiveTrue(Group group);

    Optional<GroupMember> findByGroupAndMembershipRoleAndActiveTrue(Group group, MembershipRole membershipRole);

    boolean existsByUserAndMembershipRoleAndActiveTrue(User user, MembershipRole membershipRole);

    GroupMember findByUserAndMembershipRoleAndActiveTrue(User user, MembershipRole membershipRole);

    void deleteGroupMemberByGroup(Group group);

    @Query("UPDATE GroupMember gm SET gm.active = false WHERE gm.group = :group")
    @Modifying
    @Transactional
    void deactivateGroupMembersByGroup(@Param("group") Group group);

    boolean existsByGroupAndUserAndActiveTrue(Group group, User user);
    
    List<GroupMember> findByActiveTrue();
    
    List<GroupMember> findByActiveFalse();

    Optional<GroupMember> findByUser(User user);

    boolean existsByGroupAndUser(Group group, User user);
    
    @Query("UPDATE GroupMember gm SET gm.active = false WHERE gm.group.semester.id = :semesterId")
    @Modifying
    @Transactional
    void deactivateGroupMembersBySemester(@Param("semesterId") Long semesterId);
}
