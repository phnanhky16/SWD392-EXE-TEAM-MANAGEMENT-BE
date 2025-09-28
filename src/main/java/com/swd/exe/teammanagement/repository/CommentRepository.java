package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Comment;
import com.swd.exe.teammanagement.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId);

    void deleteByPost(Post post);
}
