package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
