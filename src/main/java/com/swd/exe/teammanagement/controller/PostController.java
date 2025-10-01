package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.PostRequest;
import com.swd.exe.teammanagement.dto.response.PostResponse;
import com.swd.exe.teammanagement.enums.idea_join_post_score.PostType;
import com.swd.exe.teammanagement.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    PostService postService;
    @PostMapping("/findMember")
    public ApiResponse<PostResponse> createPostToFindMember(@RequestBody PostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .message("Create post to find member successfully")
                .result(postService.createPostToFindMember(request))
                .success(true)
                .build();
    }
    @PostMapping("/findGroup")
    public ApiResponse<PostResponse> createPostToFindGroup(@RequestBody PostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .message("Create post to find group successfully")
                .result(postService.createPostToFindGroup(request))
                .success(true)
                .build();
    }
    @GetMapping("/{id}")
    public ApiResponse<PostResponse> getPostById(@PathVariable Long id) {
        return ApiResponse.<PostResponse>builder()
                .message("Get post successfully")
                .result(postService.getPostById(id))
                .success(true)
                .build();
    }
    @GetMapping("/")
    public ApiResponse<List<PostResponse>> getAllPosts() {
        return ApiResponse.<List<PostResponse>>builder()
                .message("Get all posts successfully")
                .result(postService.getAllPosts())
                .success(true)
                .build();
    }
    @GetMapping("/{type}")
    public ApiResponse<List<PostResponse>> getPostsByType(@PathVariable PostType type) {
        return ApiResponse.<List<PostResponse>>builder()
                .message("Get posts by type successfully")
                .result(postService.getPostsByType(type))
                .success(true)
                .build();
    }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        return ApiResponse.<Void>builder()
                .message("Delete post successfully")
                .result(postService.deletePost(id))
                .success(true)
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<PostResponse> updatePost(@PathVariable Long id, @RequestBody PostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .message("Update post successfully")
                .result(postService.updatePost(id, request))
                .success(true)
                .build();
    }
}
