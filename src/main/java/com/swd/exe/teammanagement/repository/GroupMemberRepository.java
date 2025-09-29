package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    boolean existsByGroupIdAndUserIdAndRole(Long groupId, Long userId, MembershipRole role);
}
