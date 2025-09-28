package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.request.CommentRequest;
import com.swd.exe.teammanagement.dto.response.CommentResponse;
import com.swd.exe.teammanagement.entity.Comment;
import com.swd.exe.teammanagement.entity.Post;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.mapper.CommentMapper;
import com.swd.exe.teammanagement.repository.CommentRepository;
import com.swd.exe.teammanagement.repository.PostRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CommentServiceImpl implements CommentService {
    UserRepository userRepository;
    PostRepository postRepository;
    CommentRepository commentRepository;
    CommentMapper commentMapper;
    @Override
    public CommentResponse createComment(CommentRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByStudentCode(name).orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
        Comment comment = commentMapper.toComment(request);
        comment.setUser(user);
        Post post = postRepository.findById(request.getPostId()).orElseThrow(() -> new AppException(ErrorCode.POST_UNEXISTED));
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    @Override
    public CommentResponse getCommentById(Long id) {
        return null;
    }

    @Override
    public Void deleteComment(Long id) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByStudentCode(name).orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.COMMENT_UNEXISTED));
        if(!comment.getUser().getId().equals(user.getId())){
            throw new AppException(ErrorCode.DOES_NOT_DELETE_OTHER_USER_POST);}
        commentRepository.delete(comment);
        return null;
    }

    @Override
    public List<CommentResponse> getAllCommentsByPost(Long postId) {
        return List.of();
    }

    @Override
    public CommentResponse updateComment(Long id, CommentRequest request) {
        return null;
    }
}
