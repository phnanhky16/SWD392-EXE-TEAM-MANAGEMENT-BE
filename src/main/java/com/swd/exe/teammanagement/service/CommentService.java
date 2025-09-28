package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.request.CommentRequest;
import com.swd.exe.teammanagement.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(CommentRequest request);
    CommentResponse getCommentById(Long id);
    Void deleteComment(Long id);
    List<CommentResponse> getAllCommentsByPost(Long postId);
    CommentResponse updateComment(Long id, CommentRequest request);
}
