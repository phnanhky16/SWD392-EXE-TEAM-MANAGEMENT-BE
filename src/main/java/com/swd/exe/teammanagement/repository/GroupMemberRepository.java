package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    boolean existsByGroupIdAndUserIdAndRole(Long groupId, Long userId, MembershipRole role);
    Optional<GroupMember> findByUser(User user);
    boolean existsByUser(User user);
    int countByGroup(Group group);

    List<User> findUsersByGroup(Group group);

    void deleteByUser(User user);

    void deleteGroupMemberByUser(User user);

    List<User> findUsersByGroupId(Long groupId);

    int countByGroupId(Long groupId);

    Set<Major> findMajorsByGroupId(Long groupId);
}
