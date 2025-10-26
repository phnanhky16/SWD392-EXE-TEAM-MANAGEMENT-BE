package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
    Page<Message> findByGroupAndActiveTrueOrderByCreatedAtDesc(Group group, Pageable pageable);
    
    List<Message> findByGroupAndActiveTrueAndMessageTextContainingIgnoreCaseOrderByCreatedAtDesc(
            Group group, String keyword);
    
    @Query("SELECT m FROM Message m WHERE m.group = :group AND m.active = true ORDER BY m.createdAt DESC")
    List<Message> findRecentMessagesByGroup(@Param("group") Group group, Pageable pageable);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.group = :group AND m.active = true")
    Long countActiveMessagesByGroup(@Param("group") Group group);
    
    List<Message> findByActiveTrue();
    
    List<Message> findByActiveFalse();
}
