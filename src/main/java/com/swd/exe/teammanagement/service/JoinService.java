package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.request.GroupCreateFirstRequest;

public interface JoinService {
    Void joinGroupFirst(Long groupId, GroupCreateFirstRequest request);
}
