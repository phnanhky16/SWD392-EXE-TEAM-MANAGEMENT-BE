package com.swd.exe.teammanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Post;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.idea_join_post_score.PostType;

import jakarta.transaction.Transactional;

public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByTypeAndActiveTrue(PostType type);
    int countPostByGroupAndActiveTrue(Group group);
    @Transactional
    Post deletePostByGroup(Group group);

    @org.springframework.data.jpa.repository.Query("UPDATE Post p SET p.active = false WHERE p.group = :group")
    @org.springframework.data.jpa.repository.Modifying
    @Transactional
    void deactivatePostsByGroup(@org.springframework.data.repository.query.Param("group") Group group);

    int countPostByUserAndActiveTrue(User user);
    @Transactional
    void deletePostByUser(User user);

    @org.springframework.data.jpa.repository.Query("UPDATE Post p SET p.active = false WHERE p.user = :user")
    @org.springframework.data.jpa.repository.Modifying
    @Transactional
    void deactivatePostsByUser(@org.springframework.data.repository.query.Param("user") User user);

    double countPostByUserAndActive(User user, boolean active);

    double countPostByGroupAndActive(Group group, boolean active);
    
    List<Post> findByActiveTrue();
    
    List<Post> findByActiveFalse();
    
    @org.springframework.data.jpa.repository.Query("UPDATE Post p SET p.active = false WHERE p.group.semester.id = :semesterId")
    @org.springframework.data.jpa.repository.Modifying
    @Transactional
    void deactivatePostsBySemester(@org.springframework.data.repository.query.Param("semesterId") Long semesterId);
}
