package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Post;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.idea_join_post_score.PostType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByType(PostType type);
    int countPostByGroup(Group group);
    @Transactional
    Post deletePostByGroup(Group group);

    int countPostByUser(User user);
    @Transactional
    void deletePostByUser(User user);
}
