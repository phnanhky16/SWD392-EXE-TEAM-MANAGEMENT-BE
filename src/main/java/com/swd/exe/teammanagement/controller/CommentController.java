package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.CommentRequest;
import com.swd.exe.teammanagement.dto.response.CommentResponse;
import com.swd.exe.teammanagement.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Comment Management", description = "APIs for managing comments on posts")
public class CommentController {
    CommentService commentService;
    @Operation(
            summary = "Create new comment",
            description = "Create a new comment on a post. Requires authentication."
    )
    @PostMapping("/")
   ApiResponse<CommentResponse> createComment(@RequestBody CommentRequest request){return ApiResponse.<CommentResponse>builder()
            .message("Create comment successfully")
            .result(commentService.createComment(request))
            .success(true)
            .build();}
    @Operation(
            summary = "Update comment",
            description = "Update an existing comment. Only comment author or admin can update."
    )
    @PutMapping("/{id}")
    ApiResponse<CommentResponse> updateComment(@PathVariable Long id,@RequestBody CommentRequest request){return ApiResponse.<CommentResponse>builder()
            .message("Update comment successfully")
            .result(commentService.updateComment(id,request))
            .success(true)
            .build();}
    @Operation(
            summary = "Delete comment",
            description = "Delete a comment. Only comment author or admin can delete."
    )
    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteComment(@PathVariable Long id){return ApiResponse.<Void>builder()
            .message("Delete comment successfully")
            .result(commentService.deleteComment(id))
            .success(true)
            .build();}
    @Operation(
            summary = "Get comment by ID",
            description = "Retrieve a specific comment by its unique identifier"
    )
    @GetMapping("/{id}")
    ApiResponse<CommentResponse> getCommentById(@PathVariable Long id){return ApiResponse.<CommentResponse>builder()
            .message("Get comment successfully")
            .result(commentService.getCommentById(id))
            .success(true)
            .build();}
    @Operation(
            summary = "Get all comments by post",
            description = "Retrieve all comments for a specific post"
    )
    @GetMapping("/post/{postId}")
    ApiResponse<List<CommentResponse>> getAllCommentsByPosts(@PathVariable Long postId){return ApiResponse.<List<CommentResponse>>builder()
            .message("Get all comments by post successfully")
            .result(commentService.getAllCommentsByPost(postId))
            .success(true)
            .build();}
    @Operation(
            summary = "Get all comments",
            description = "Retrieve all comments from all posts"
    )
    @GetMapping("/")
    ApiResponse<List<CommentResponse>> getAllComments(){return ApiResponse.<List<CommentResponse>>builder()
            .message("Get all comments by post successfully")
            .result(commentService.getAllComments())
            .success(true)
            .build();}
}
