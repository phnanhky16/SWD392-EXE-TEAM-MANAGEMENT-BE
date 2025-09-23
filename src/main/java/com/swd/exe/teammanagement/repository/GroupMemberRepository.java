package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
}
