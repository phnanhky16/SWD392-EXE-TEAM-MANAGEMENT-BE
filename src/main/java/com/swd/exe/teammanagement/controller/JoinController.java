package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.GroupCreateFirstRequest;
import com.swd.exe.teammanagement.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/joins")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Join Management", description = "APIs for joining and creating groups")
public class JoinController {
    JoinService joinService;

    @Operation(
            summary = "Join group as first (create and become leader)",
            description = "User joins an empty group and sets title/description; becomes leader. Requires authentication."
    )
    @PostMapping("/first/{groupId}")
    public ApiResponse<Void> joinGroupFirst(
            @PathVariable("groupId") Long groupId,
            @Valid @RequestBody GroupCreateFirstRequest request
    ) {
        joinService.joinGroupFirst(groupId, request);
        return ApiResponse.created("Joined group and created successfully", null);
    }
}