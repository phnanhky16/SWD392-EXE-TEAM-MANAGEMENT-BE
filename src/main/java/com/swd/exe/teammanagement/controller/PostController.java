package com.swd.exe.teammanagement.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Post Management", description = "APIs for managing recruitment posts (find member/group)")
public class PostController {
    PostService postService;
    
    @Operation(
            summary = "Create post to find member",
            description = "Group leader creates a post to recruit members for their group. Only group leaders can create this type of post."
    )
    @PostMapping("/findMember")
    public ApiResponse<PostResponse> createPostToFindMember(@Valid @RequestBody PostRequest request) {
        return ApiResponse.created("Create post to find member successfully", postService.createPostToFindMember(request));
    }
    
    @Operation(
            summary = "Create post to find group",
            description = "Individual student creates a post to find and join a group. Students without group can create this type of post."
    )
    @PostMapping("/findGroup")
    public ApiResponse<PostResponse> createPostToFindGroup(@Valid @RequestBody PostRequest request) {
        return ApiResponse.created("Create post to find group successfully", postService.createPostToFindGroup(request));
    }
    
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
    @GetMapping("/")
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
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ApiResponse.success("Delete post successfully", null);
    }
    
    @Operation(
            summary = "Update post",
            description = "Update a recruitment post. Only the post author can update their own post."
    )
    @PutMapping("/{id}")
    public ApiResponse<PostResponse> updatePost(@PathVariable Long id, @Valid @RequestBody PostRequest request) {
        return ApiResponse.success("Update post successfully", postService.updatePost(id, request));
    }
}
