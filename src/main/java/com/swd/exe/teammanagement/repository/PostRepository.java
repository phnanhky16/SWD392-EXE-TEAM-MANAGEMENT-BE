package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {
}
