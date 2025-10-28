package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.request.InviteRequest;
import com.swd.exe.teammanagement.dto.request.InviteUpdateRequest;
import com.swd.exe.teammanagement.dto.response.InviteResponse;
import com.swd.exe.teammanagement.dto.response.MyInviteResponse;
import com.swd.exe.teammanagement.enums.invite.InviteStatus;

public interface InviteService {
    InviteResponse createInvite(InviteRequest request);
    MyInviteResponse getMyInvites(InviteStatus status, int receivedPage, int receivedSize, int sentPage, int sentSize);
    InviteResponse respondToInvite(Long inviteId, InviteUpdateRequest request);
}
