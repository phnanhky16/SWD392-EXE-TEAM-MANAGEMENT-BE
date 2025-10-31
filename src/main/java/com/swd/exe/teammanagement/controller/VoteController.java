package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.entity.Vote;
import com.swd.exe.teammanagement.entity.VoteChoice;
import com.swd.exe.teammanagement.enums.vote.ChoiceValue;
import com.swd.exe.teammanagement.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Vote Management", description = "APIs for managing votes and voting choices")
public class VoteController {
    VoteService voteService;

    @Operation(
            summary = "Create vote for join request",
            description = "Create a vote for a user to join a group. Automatically creates voting session for group members."
    )
    @PostMapping("/join/{groupId}/{userId}")
    public ApiResponse<Vote> createVoteJoin(
            @PathVariable Long groupId,
            @PathVariable Long userId
    ) {
        return ApiResponse.created("Vote created successfully", 
                voteService.voteJoin(groupId, userId));
    }

    @Operation(
            summary = "Submit vote choice",
            description = "Submit a vote choice (YES or NO) for a specific vote. Only group members can vote."
    )
    @PostMapping("/{voteId}/choice")
    public ApiResponse<VoteChoice> submitVoteChoice(
            @PathVariable Long voteId,
            @RequestParam ChoiceValue choiceValue
    ) {
        return ApiResponse.created("Vote choice submitted successfully", 
                voteService.voteChoice(voteId, choiceValue));
    }

    @Operation(
            summary = "Get vote by ID",
            description = "Retrieve details of a specific vote by its ID."
    )
    @GetMapping("/{voteId}")
    public ApiResponse<Vote> getVoteById(@PathVariable Long voteId) {
        return ApiResponse.success("Get vote successfully", 
                voteService.getVoteById(voteId));
    }

    @Operation(
            summary = "Get vote choices",
            description = "Get all vote choices for a specific vote, showing how members voted."
    )
    @GetMapping("/{voteId}/choices")
    public ApiResponse<List<VoteChoice>> getVoteChoices(@PathVariable Long voteId) {
        return ApiResponse.success("Get vote choices successfully", 
                voteService.getVoteChoices(voteId));
    }

    @Operation(
            summary = "Get open votes",
            description = "Get all currently open votes in the system."
    )
    @GetMapping("/open")
    public ApiResponse<List<Vote>> getOpenVotes() {
        return ApiResponse.success("Get open votes successfully", 
                voteService.getOpenVotes());
    }

    @Operation(
            summary = "Get votes by group",
            description = "Get all votes for a specific group, including both open and closed votes."
    )
    @GetMapping("/group/{groupId}")
    public ApiResponse<List<Vote>> getVotesByGroup(@PathVariable Long groupId) {
        return ApiResponse.success("Get group votes successfully", 
                voteService.getVotesByGroup(groupId));
    }

    @Operation(
            summary = "Finalize vote",
            description = "Manually finalize a vote and process the results. Normally votes are auto-closed by scheduler."
    )
    @PatchMapping("/{voteId}/finalize")
    public ApiResponse<Void> finalizeVote(@PathVariable Long voteId) {
        voteService.voteDone(voteId);
        return ApiResponse.success("Vote finalized successfully", null);
    }
    @Operation(
            summary = "Activate a vote",
            description = "Set a vote to active = true. Only admins or group leaders can perform this action."
    )
    @PutMapping("/{voteId}/activate")
    public ApiResponse<Vote> activateVote(@PathVariable Long voteId) {
        return ApiResponse.success("Vote activated successfully",
                voteService.activateVote(voteId));
    }

    @Operation(
            summary = "Deactivate a vote",
            description = "Set a vote to active = false. Only admins or group leaders can perform this action."
    )
    @PutMapping("/{voteId}/deactivate")
    public ApiResponse<Vote> deactivateVote(@PathVariable Long voteId) {
        return ApiResponse.success("Vote deactivated successfully",
                voteService.deactivateVote(voteId));
    }

    @Operation(
            summary = "Toggle vote active status",
            description = "Change vote's active status between active/inactive."
    )
    @PutMapping("/{voteId}/toggle-active")
    public ApiResponse<Vote> toggleVoteActive(@PathVariable Long voteId) {
        return ApiResponse.success("Vote active status toggled successfully",
                voteService.changeVoteActiveStatus(voteId));
    }
}
