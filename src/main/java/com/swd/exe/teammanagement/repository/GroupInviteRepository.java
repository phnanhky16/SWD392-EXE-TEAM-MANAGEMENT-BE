package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupInvite;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.invite.InviteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupInviteRepository extends JpaRepository<GroupInvite, Long> {
    boolean existsByGroupAndInviteeAndStatus(Group group, User invitee, InviteStatus status);
    Page<GroupInvite> findByInvitee(User invitee, Pageable pageable);
    Page<GroupInvite> findByInviteeAndStatus(User invitee, InviteStatus status, Pageable pageable);
    Page<GroupInvite> findByInviter(User inviter, Pageable pageable);
    Page<GroupInvite> findByInviterAndStatus(User inviter, InviteStatus status, Pageable pageable);
}
