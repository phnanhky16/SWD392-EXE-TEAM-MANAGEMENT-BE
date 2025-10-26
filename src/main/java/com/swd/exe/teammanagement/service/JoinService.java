package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.entity.Join;

import java.util.List;

public interface JoinService {
    Void joinGroup(Long groupId);
    Void joinRequest(Long groupId, Long userId);
    List<Join> getPendingJoinRequests(Long groupId);
    List<Join> getMyJoinRequests();
    Void cancelJoinRequest(Long joinId);
    Join activateJoin(Long joinId);
    Join deactivateJoin(Long joinId);
    Join changeJoinActiveStatus(Long joinId);
}
