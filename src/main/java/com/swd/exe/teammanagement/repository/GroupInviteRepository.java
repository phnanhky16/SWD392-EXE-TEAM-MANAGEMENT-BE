package com.swd.exe.teammanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupInvite;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.invite.InviteStatus;

public interface GroupInviteRepository extends JpaRepository<GroupInvite, Long> {
    boolean existsByGroupAndInviteeAndStatusAndActiveTrue(Group group, User invitee, InviteStatus status);
    Page<GroupInvite> findByInviteeAndActiveTrue(User invitee, Pageable pageable);
    Page<GroupInvite> findByInviteeAndStatusAndActiveTrue(User invitee, InviteStatus status, Pageable pageable);
    Page<GroupInvite> findByInviterAndActiveTrue(User inviter, Pageable pageable);
    Page<GroupInvite> findByInviterAndStatusAndActiveTrue(User inviter, InviteStatus status, Pageable pageable);
    
    @Modifying
    @Query("UPDATE GroupInvite gi SET gi.active = false WHERE gi.group.semester.id = :semesterId")
    void deactivateInvitesBySemester(@Param("semesterId") Long semesterId);
}
