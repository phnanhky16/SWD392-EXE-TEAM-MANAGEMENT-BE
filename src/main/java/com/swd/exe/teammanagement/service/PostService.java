package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.request.PostRequest;
import com.swd.exe.teammanagement.dto.request.PostUpdateRequest;
import com.swd.exe.teammanagement.dto.response.PostResponse;
import com.swd.exe.teammanagement.enums.idea_join_post_score.PostType;

import java.util.List;

public interface PostService {
     PostResponse createPost(PostRequest request);
     PostResponse getPostById(Long id);
     List<PostResponse> getAllPosts();
     Void deletePost(Long id);
     List<PostResponse> getPostsByType(PostType type);
     PostResponse updatePost(Long id, PostUpdateRequest request);
     PostResponse activatePost(Long id);
     PostResponse deactivatePost(Long id);
     PostResponse changePostActiveStatus(Long id);
}
