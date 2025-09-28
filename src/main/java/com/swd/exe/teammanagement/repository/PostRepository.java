package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Post;
import com.swd.exe.teammanagement.enums.idea_join_post.PostType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByType(PostType type);
}
