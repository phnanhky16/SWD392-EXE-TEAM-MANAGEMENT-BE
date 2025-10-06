package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.request.GroupCreateRequest;

public interface JoinService {
    Void joinGroupFirst(Long groupId, GroupCreateRequest request);
    Void joinGroup(Long groupId);
}
