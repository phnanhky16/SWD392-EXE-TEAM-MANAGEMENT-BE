package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.IdeaRequest;
import com.swd.exe.teammanagement.dto.response.IdeaResponse;
import com.swd.exe.teammanagement.service.IdeaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ideas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Idea Management", description = "APIs for managing group ideas")
public class IdeaController {

    IdeaService ideaService;

    @Operation(
            summary = "Create new idea",
            description = "Leader tạo ý tưởng cho group"
    )
    @PostMapping("/")
    ApiResponse<IdeaResponse> createIdea(@Valid @RequestBody IdeaRequest request) {
        return ApiResponse.<IdeaResponse>builder()
                .message("Create idea successfully")
                .result(ideaService.createIdea(request))
                .success(true)
                .build();
    }

    @Operation(
            summary = "Update idea",
            description = "Leader cập nhật nội dung ý tưởng (chỉ khi chưa submit/locked)"
    )
    @PutMapping("/{id}")
    ApiResponse<IdeaResponse> updateIdea(@PathVariable Long id,
                                         @Valid @RequestBody IdeaRequest request) {
        return ApiResponse.<IdeaResponse>builder()
                .message("Update idea successfully")
                .result(ideaService.updateIdea(id, request))
                .success(true)
                .build();
    }

    @Operation(
            summary = "Delete idea",
            description = "Xoá ý tưởng (chỉ leader/admin, tuỳ trạng thái)"
    )
    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteIdea(@PathVariable Long id) {
        return ApiResponse.<Void>builder()
                .message("Delete idea successfully")
                .result(ideaService.deleteIdea(id))
                .success(true)
                .build();
    }

    @Operation(
            summary = "Get idea by ID",
            description = "Lấy chi tiết 1 ý tưởng"
    )
    @GetMapping("/{id}")
    ApiResponse<IdeaResponse> getIdeaById(@PathVariable Long id) {
        return ApiResponse.<IdeaResponse>builder()
                .message("Get idea successfully")
                .result(ideaService.getIdeaById(id))
                .success(true)
                .build();
    }

    @Operation(
            summary = "Get all ideas by group",
            description = "Lấy danh sách ý tưởng của một group"
    )
    @GetMapping("/group/{groupId}")
    ApiResponse<List<IdeaResponse>> getAllIdeasByGroup(@PathVariable Long groupId) {
        return ApiResponse.<List<IdeaResponse>>builder()
                .message("Get all ideas by group successfully")
                .result(ideaService.getAllIdeasByGroup(groupId))
                .success(true)
                .build();
    }

    @Operation(
            summary = "Get all ideas",
            description = "Lấy tất cả ý tưởng (admin/teacher)"
    )
    @GetMapping("/")
    ApiResponse<List<IdeaResponse>> getAllIdeas() {
        return ApiResponse.<List<IdeaResponse>>builder()
                .message("Get all ideas successfully")
                .result(ideaService.getAllIdeas())
                .success(true)
                .build();
    }

    // ====== các action theo vòng đời Idea ======

    @Operation(
            summary = "Submit idea",
            description = "Leader nộp ý tưởng để GV duyệt (chuyển DRAFT -> PROPOSED)"
    )
    @PatchMapping("/{id}/submit")
    ApiResponse<IdeaResponse> submitIdea(@PathVariable Long id) {
        return ApiResponse.<IdeaResponse>builder()
                .message("Submit idea successfully")
                .result(ideaService.submitIdea(id))
                .success(true)
                .build();
    }

    @Operation(
            summary = "Approve idea",
            description = "Teacher duyệt ý tưởng (PROPOSED -> APPROVED)"
    )
    @PatchMapping("/{id}/approve")
    ApiResponse<IdeaResponse> approveIdea(@PathVariable Long id) {
        return ApiResponse.<IdeaResponse>builder()
                .message("Approve idea successfully")
                .result(ideaService.approveIdea(id))
                .success(true)
                .build();
    }

    @Operation(
            summary = "Reject idea",
            description = "Teacher từ chối ý tưởng (PROPOSED -> REJECTED) kèm lý do"
    )
    @PatchMapping("/{id}/reject")
    ApiResponse<IdeaResponse> rejectIdea(@PathVariable Long id,
                                         @RequestParam String reason) {
        return ApiResponse.<IdeaResponse>builder()
                .message("Reject idea successfully")
                .result(ideaService.rejectIdea(id, reason))
                .success(true)
                .build();
    }
}
