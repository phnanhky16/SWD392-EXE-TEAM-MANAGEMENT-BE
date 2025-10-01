package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.request.PostRequest;
import com.swd.exe.teammanagement.dto.response.PostResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Post;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.idea_join_post_score.PostType;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.mapper.PostMapper;
import com.swd.exe.teammanagement.repository.CommentRepository;
import com.swd.exe.teammanagement.repository.GroupRepository;
import com.swd.exe.teammanagement.repository.PostRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class PostServiceImpl implements PostService {
    PostRepository postRepository;
    PostMapper postMapper;
    UserRepository userRepository;
    GroupRepository groupRepository;
    CommentRepository commentRepository;
    @Override
    // Create post to find member, only group leader can create this type of post
    public PostResponse createPostToFindMember(PostRequest request) {
        Post post = postMapper.toPost(request);
        User user = getCurrentUser();
        Group group = groupRepository.findByLeader(user).orElseThrow(() -> new AppException(ErrorCode.GROUP_UNEXISTED));
        if(postRepository.countPostByGroup(group)==1){
            throw new AppException(ErrorCode.JUST_ONE_POST_ONE_GROUP);
        }
        post.setUser(user);
        post.setGroup(group);
        post.setType(PostType.FIND_MEMBER);
        post.setCreatedAt(LocalDateTime.now());
        return postMapper.toPostResponse(postRepository.save(post));
    }

    @Override
    public PostResponse createPostToFindGroup(PostRequest request) {
        Post post = postMapper.toPost(request);
        User user = getCurrentUser();
        post.setUser(user);
        post.setType(PostType.FIND_GROUP);
        post.setCreatedAt(LocalDateTime.now());
        return postMapper.toPostResponse(postRepository.save(post));
    }

    @Override
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.POST_UNEXISTED));
        return postMapper.toPostResponse(post);
    }

    @Override
    public List<PostResponse> getAllPosts() {
        return postMapper.toPostResponseList(postRepository.findAll()) ;
    }

    @Override
    public Void deletePost(Long id) {
        User user = getCurrentUser();
        Post post = postRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.POST_UNEXISTED));
        if (!post.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.DOES_NOT_DELETE_OTHER_USER_POST);
        }
        postRepository.delete(post);
        commentRepository.deleteByPost(post);
        return null;
    }

    @Override
    public List<PostResponse> getPostsByType(PostType type) {
        return postMapper.toPostResponseList(postRepository.findByType(type));
    }

    @Override
    public PostResponse updatePost(Long id, PostRequest request) {
        User user = getCurrentUser();
        Post post = postRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.POST_UNEXISTED));
        if (!post.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.DOES_NOT_DELETE_OTHER_USER_POST);
        }
        post.setCreatedAt(LocalDateTime.now());
        postMapper.toUpdatePost(post, request);
        return postMapper.toPostResponse(postRepository.save(post));
    }
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
    }
}
