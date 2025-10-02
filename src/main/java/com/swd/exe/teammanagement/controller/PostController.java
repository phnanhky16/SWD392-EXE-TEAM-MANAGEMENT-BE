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
        return ApiResponse.created("Create post to find member successfully", postService.createPostToFindMember(request));
    }
    @PostMapping("/findGroup")
    public ApiResponse<PostResponse> createPostToFindGroup(@RequestBody PostRequest request) {
        return ApiResponse.created("Create post to find group successfully", postService.createPostToFindGroup(request));
    }
    @GetMapping("/{id}")
    public ApiResponse<PostResponse> getPostById(@PathVariable Long id) {
        return ApiResponse.success("Get post successfully", postService.getPostById(id));
    }
    @GetMapping("/")
    public ApiResponse<List<PostResponse>> getAllPosts() {
        return ApiResponse.success("Get all posts successfully", postService.getAllPosts());
    }
    @GetMapping("/type/{type}")
    public ApiResponse<List<PostResponse>> getPostsByType(@PathVariable PostType type) {
        return ApiResponse.success("Get posts by type successfully", postService.getPostsByType(type));
    }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ApiResponse.success("Delete post successfully", null);
    }
    @PutMapping("/{id}")
    public ApiResponse<PostResponse> updatePost(@PathVariable Long id, @RequestBody PostRequest request) {
        return ApiResponse.success("Update post successfully", postService.updatePost(id, request));
    }
}
