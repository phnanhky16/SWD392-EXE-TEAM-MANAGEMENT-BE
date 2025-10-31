package com.swd.exe.teammanagement.controller;

import java.util.List;

import com.swd.exe.teammanagement.dto.request.PostUpdateRequest;
import org.springframework.security.access.prepost.PostAuthorize;
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
import com.swd.exe.teammanagement.dto.request.PostRequest;
import com.swd.exe.teammanagement.dto.response.PostResponse;
import com.swd.exe.teammanagement.enums.idea_join_post_score.PostType;
import com.swd.exe.teammanagement.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Post Management", description = "APIs for managing recruitment posts (find member/group)")
public class PostController {
    PostService postService;
    
    @Operation(
            summary = "Create post",
            description = "Create a recruitment post. Depending on type, user or group will be set."
    )
    @PostMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('MODERATER')")
    public ApiResponse<PostResponse> createPost(@Valid @RequestBody PostRequest request) {
        return ApiResponse.created("Create post successfully", postService.createPost(request));
    }
    
//    @Operation(
//            summary = "Create post to find member",
//            description = "Group leader creates a post to recruit members for their group. Only group leaders can create this type of post."
//    )
//    @PostMapping("/findMember")
//    public ApiResponse<PostResponse> createPostToFindMember(@Valid @RequestBody PostRequest request) {
//        return ApiResponse.created("Create post to find member successfully", postService.createPostToFindMember(request));
//    }
//
//    @Operation(
//            summary = "Create post to find group",
//            description = "Individual student creates a post to find and join a group. Students without group can create this type of post."
//    )
//    @PostMapping("/findGroup")
//    public ApiResponse<PostResponse> createPostToFindGroup(@Valid @RequestBody PostRequest request) {
//        return ApiResponse.created("Create post to find group successfully", postService.createPostToFindGroup(request));
//    }
    
    @Operation(
            summary = "Get post by ID",
            description = "Retrieve a specific post by its unique identifier"
    )
    @GetMapping("/{id}")
    public ApiResponse<PostResponse> getPostById(@PathVariable Long id) {
        return ApiResponse.success("Get post successfully", postService.getPostById(id));
    }
    
    @Operation(
            summary = "Get all posts",
            description = "Retrieve all recruitment posts from all users"
    )
    @GetMapping
    public ApiResponse<List<PostResponse>> getAllPosts() {
        return ApiResponse.success("Get all posts successfully", postService.getAllPosts());
    }
    
    @Operation(
            summary = "Get posts by type",
            description = "Filter posts by type: FIND_MEMBER (groups looking for members) or FIND_GROUP (individuals looking for groups)"
    )
    @GetMapping("/type/{type}")
    public ApiResponse<List<PostResponse>> getPostsByType(@PathVariable PostType type) {
        return ApiResponse.success("Get posts by type successfully", postService.getPostsByType(type));
    }
    
    @Operation(
            summary = "Delete post",
            description = "Delete a recruitment post. Only the post author can delete their own post."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('MODERATER')")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ApiResponse.success("Delete post successfully", null);
    }
    
    @Operation(
            summary = "Update post",
            description = "Update a recruitment post. Only the post author can update their own post."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('MODERATER')")
    public ApiResponse<PostResponse> updatePost(@PathVariable Long id, @Valid @RequestBody PostUpdateRequest request) {
        return ApiResponse.success("Update post successfully", postService.updatePost(id, request));
    }
    @Operation(
            summary = "Activate post",
            description = "Activate a specific post (set active = true). Only the post author or an admin can perform this action."
    )
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('MODERATER')")
    public ApiResponse<PostResponse> activatePost(@PathVariable Long id) {
        return ApiResponse.success("Activate post successfully", postService.activatePost(id));
    }

    @Operation(
            summary = "Deactivate post",
            description = "Deactivate a specific post (set active = false). Only the post author or an admin can perform this action."
    )
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('MODERATER')")
    public ApiResponse<PostResponse> deactivatePost(@PathVariable Long id) {
        return ApiResponse.success("Deactivate post successfully", postService.deactivatePost(id));
    }

    @Operation(
            summary = "Toggle post active status",
            description = "Change post active status between active/inactive. Only the post author or an admin can perform this action."
    )
    @PutMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('MODERATER')")
    public ApiResponse<PostResponse> togglePostActive(@PathVariable Long id) {
        return ApiResponse.success("Change post active status successfully", postService.changePostActiveStatus(id));
    }
}
