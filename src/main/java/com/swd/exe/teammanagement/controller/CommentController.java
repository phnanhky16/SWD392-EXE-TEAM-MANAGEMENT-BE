package com.swd.exe.teammanagement.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.CommentRequest;
import com.swd.exe.teammanagement.dto.response.CommentResponse;
import com.swd.exe.teammanagement.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Comment Management", description = "APIs for managing comments on posts")
public class CommentController {
    CommentService commentService;
    @Operation(
            summary = "Create new comment",
            description = "Create a new comment on a post. Requires authentication."
    )
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
   ApiResponse<CommentResponse> createComment(@RequestBody CommentRequest request){
        return ApiResponse.created("Create comment successfully", commentService.createComment(request));
    }
    @Operation(
            summary = "Update comment",
            description = "Update an existing comment. Only comment author or admin can update."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    ApiResponse<CommentResponse> updateComment(@PathVariable Long id,@RequestBody CommentRequest request){
        return ApiResponse.success("Update comment successfully", commentService.updateComment(id,request));
    }
    @Operation(
            summary = "Delete comment",
            description = "Delete a comment. Only comment author or admin can delete."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    ApiResponse<String> deleteComment(@PathVariable Long id){
        return ApiResponse.success("Delete comment successfully", commentService.deleteComment(id));
    }
    @Operation(
            summary = "Get comment by ID",
            description = "Retrieve a specific comment by its unique identifier"
    )
    @GetMapping("/{id}")
    ApiResponse<CommentResponse> getCommentById(@PathVariable Long id){
        return ApiResponse.success("Get comment successfully", commentService.getCommentById(id));
    }
    @Operation(
            summary = "Get all comments by post",
            description = "Retrieve all comments for a specific post"
    )
    @GetMapping("/post/{postId}")
    ApiResponse<List<CommentResponse>> getAllCommentsByPosts(@PathVariable Long postId){
        return ApiResponse.success("Get all comments by post successfully", commentService.getAllCommentsByPost(postId));
    }
    @Operation(
            summary = "Get all comments",
            description = "Retrieve all comments from all posts"
    )
    @GetMapping
    ApiResponse<List<CommentResponse>> getAllComments(){
        return ApiResponse.success("Get all comments successfully", commentService.getAllComments());
    }
}
