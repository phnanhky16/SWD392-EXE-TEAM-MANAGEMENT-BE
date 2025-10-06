package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.IdeaRequest;
import com.swd.exe.teammanagement.dto.response.IdeaResponse;
import com.swd.exe.teammanagement.service.IdeaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/ideas")
@Tag(name = "Idea Management", description = "APIs for managing group ideas")

public class IdeaController {

    IdeaService ideaService;

    @Operation(
            summary = "Create new idea",
            description = "Leader tạo ý tưởng cho group"
    )
    @PostMapping
    ApiResponse<IdeaResponse> createIdea(@Valid @RequestBody IdeaRequest request) {
        return ApiResponse.created("Create idea successfully", ideaService.createIdea(request));
    }

    @Operation(
            summary = "Update idea",
            description = "Leader cập nhật nội dung ý tưởng (chỉ DRAFT/REJECTED mới được sửa)"
    )
    @PutMapping("/{id}")
    ApiResponse<IdeaResponse> updateIdea(@PathVariable Long id,
                                         @Valid @RequestBody IdeaRequest request) {
        return ApiResponse.success("Update idea successfully", ideaService.updateIdea(id, request));
    }

    @Operation(
            summary = "Delete idea",
            description = "Xoá ý tưởng (chỉ leader/admin, tuỳ trạng thái)"
    )
    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteIdea(@PathVariable Long id) {
        ideaService.deleteIdea(id);
        return ApiResponse.success("Delete idea successfully", null);
    }

    @Operation(
            summary = "Get idea by ID",
            description = "Lấy chi tiết 1 ý tưởng"
    )
    @GetMapping("/{id}")
    ApiResponse<IdeaResponse> getIdeaById(@PathVariable Long id) {
        return ApiResponse.success("Get idea successfully", ideaService.getIdeaById(id));
    }

    @Operation(
            summary = "Get all ideas by group",
            description = "Lấy danh sách ý tưởng của một group"
    )
    @GetMapping("/group/{groupId}")
    ApiResponse<List<IdeaResponse>> getAllIdeasByGroup(@PathVariable Long groupId) {
        return ApiResponse.success("Get all ideas by group successfully", ideaService.getAllIdeasByGroup(groupId));
    }

    @Operation(
            summary = "Get all ideas",
            description = "Lấy tất cả ý tưởng (admin/teacher)"
    )
    @GetMapping
    ApiResponse<List<IdeaResponse>> getAllIdeas() {
        return ApiResponse.success("Get all ideas successfully", ideaService.getAllIdeas());
    }

    // ====== các action theo vòng đời Idea ======

    @Operation(
            summary = "Submit idea",
            description = "Leader nộp ý tưởng để GV duyệt (chuyển DRAFT -> PROPOSED)"
    )
    @PatchMapping("/{id}/submit")
    ApiResponse<IdeaResponse> submitIdea(@PathVariable Long id) {
        return ApiResponse.success("Submit idea successfully", ideaService.submitIdea(id));
    }

    @Operation(
            summary = "Approve idea",
            description = "Teacher duyệt ý tưởng (PROPOSED -> APPROVED)"
    )
    @PatchMapping("/{id}/approve")
    ApiResponse<IdeaResponse> approveIdea(@PathVariable Long id) {
        return ApiResponse.success("Approve idea successfully", ideaService.approveIdea(id));
    }

    @Operation(
            summary = "Reject idea",
            description = "Teacher từ chối ý tưởng (PROPOSED -> REJECTED) kèm lý do"
    )
    @PatchMapping("/{id}/reject")
    ApiResponse<IdeaResponse> rejectIdea(@PathVariable Long id,
                                         @RequestParam String reason) {
        return ApiResponse.success("Reject idea successfully", ideaService.rejectIdea(id, reason));
    }
}
