package com.swd.exe.teammanagement.service;

import java.util.List;

import com.swd.exe.teammanagement.dto.request.CommentRequest;
import com.swd.exe.teammanagement.dto.response.CommentResponse;

public interface CommentService {
    CommentResponse createComment(CommentRequest request);
    CommentResponse getCommentById(Long id);
    String deleteComment(Long id);
    List<CommentResponse> getAllCommentsByPost(Long postId);
    CommentResponse updateComment(Long id, CommentRequest request);
    List<CommentResponse> getAllComments();
}
