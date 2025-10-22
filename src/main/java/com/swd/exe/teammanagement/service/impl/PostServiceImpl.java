package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.request.PostRequest;
import com.swd.exe.teammanagement.dto.response.PostResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.Post;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.idea_join_post_score.PostType;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.mapper.GroupMapper;
import com.swd.exe.teammanagement.mapper.PostMapper;
import com.swd.exe.teammanagement.mapper.UserMapper;
import com.swd.exe.teammanagement.repository.*;
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
    CommentRepository commentRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;

    @Override
    public PostResponse createPost(PostRequest request) {
        User user =getCurrentUser();
        Post post = new Post();
        if(request.getPostType().equals(PostType.FIND_GROUP)) {
            if (groupMemberRepository.existsByUser(user)) {
                throw new AppException(ErrorCode.USER_ALREADY_IN_GROUP);
            }
            if (postRepository.countPostByUserAndActive(user,true) == 1) {
                throw new AppException(ErrorCode.POST_ALREADY_ACTIVE);
            }
            post.setContent(request.getContent());
            post.setCreatedAt(LocalDateTime.now());
            post.setType(PostType.FIND_GROUP);
            post.setUser(user);
            postRepository.save(post);
        }else{
            GroupMember gm = groupMemberRepository.findByUser(user).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
            if (gm.getMembershipRole() != MembershipRole.LEADER) {
                throw new AppException(ErrorCode.ONLY_GROUP_LEADER);
            }
            Group group = gm.getGroup();
            if (postRepository.countPostByGroupAndActive(group,true) == 1) {
                throw new AppException(ErrorCode.POST_ALREADY_ACTIVE);
            }
            post.setContent(request.getContent());
            post.setCreatedAt(LocalDateTime.now());
            post.setType(PostType.FIND_MEMBER);
            post.setGroup(group);
            postRepository.save(post);
        }
        return PostResponse.builder().id(post.getId()).content(post.getContent()).createdAt(post.getCreatedAt()).type(post.getType()).userResponse(userMapper.toUserResponse(post.getUser())).groupResponse(groupMapper.toGroupResponse(post.getGroup())).build();
    }

    @Override
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.POST_UNEXISTED));
        return postMapper.toPostResponse(post);
    }

    @Override
    public List<PostResponse> getAllPosts() {
        return postMapper.toPostResponseList(postRepository.findAll());
    }

    @Override
    public Void deletePost(Long id) {
        User user = getCurrentUser();
        Post post = postRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.POST_UNEXISTED));
        if (post.getGroup()==null) {
            if(post.getUser().equals(user)){
                post.setActive(!post.isActive());
                postRepository.save(post);
                return null;
            }
        }else{
            GroupMember gm = groupMemberRepository.findByUser(user).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
            if (gm.getMembershipRole() != MembershipRole.LEADER) {
                throw new AppException(ErrorCode.ONLY_GROUP_LEADER);
            }
            if(!post.getGroup().equals(gm.getGroup())){
                throw new AppException(ErrorCode.POST_OF_ANOTHER_GROUP);
            }
            post.setActive(!post.isActive());
            postRepository.save(post);
            return null;
        }
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
