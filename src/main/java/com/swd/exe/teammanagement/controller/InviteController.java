package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.InviteRequest;
import com.swd.exe.teammanagement.dto.request.InviteUpdateRequest;
import com.swd.exe.teammanagement.dto.response.InviteResponse;
import com.swd.exe.teammanagement.dto.response.MyInviteResponse;
import com.swd.exe.teammanagement.enums.invite.InviteStatus;
import com.swd.exe.teammanagement.service.InviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invites")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Invite Management", description = "APIs for managing group invitations")
public class InviteController {

    InviteService inviteService;

    @Operation(summary = "Create a group invite", description = "Only group leaders can invite a user to join their group.")
    @PostMapping
    @PostAuthorize("hasRole('USER')")
    public ApiResponse<InviteResponse> createInvite(@Valid @RequestBody InviteRequest request) {
        return ApiResponse.created("Invite created successfully", inviteService.createInvite(request));
    }

    @Operation(summary = "Get my invites", description = "Retrieve invites I sent and received with optional status filter and pagination.")
    @GetMapping("/my")
    public ApiResponse<MyInviteResponse> getMyInvites(
            @RequestParam(required = false) InviteStatus status,
            @RequestParam(defaultValue = "1") int receivedPage,
            @RequestParam(defaultValue = "10") int receivedSize,
            @RequestParam(defaultValue = "1") int sentPage,
            @RequestParam(defaultValue = "10") int sentSize
    ) {
        return ApiResponse.success("Get invites successfully",
                inviteService.getMyInvites(status, receivedPage, receivedSize, sentPage, sentSize));
    }

    @Operation(summary = "Respond to an invite", description = "Accept or decline an invite. Only the invitee can respond.")
    @PatchMapping("/{inviteId}")
    @PostAuthorize("hasRole('USER')")
    public ApiResponse<InviteResponse> respondToInvite(
            @PathVariable Long inviteId,
            @Valid @RequestBody InviteUpdateRequest request
    ) {
        return ApiResponse.success("Invite updated successfully", inviteService.respondToInvite(inviteId, request));
    }
}
