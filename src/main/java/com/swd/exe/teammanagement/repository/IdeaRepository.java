package com.swd.exe.teammanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Idea;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.idea_join_post_score.IdeaStatus;

import jakarta.transaction.Transactional;

public interface IdeaRepository extends JpaRepository<Idea, Long> {

    List<Idea> findAllByGroupIdAndActiveTrueOrderByCreatedAtDesc(Long groupId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Idea i SET i.active = false WHERE i.group = :group")
    void deleteIdeaByGroup(@Param("group") Group group);
    
    @Modifying
    @Transactional
    @Query("UPDATE Idea i SET i.active = false WHERE i.group = :group")
    void deleteIdeasByGroup(@Param("group") Group group);
    
    // Dùng cho GET /api/ideas/my
    Page<Idea> findByReviewer_IdOrderByCreatedAtDesc(Long reviewerId, Pageable pageable);
    Page<Idea> findByReviewer_IdAndStatusOrderByCreatedAtDesc(Long reviewerId, IdeaStatus status, Pageable pageable);

    // Gán reviewer cho toàn bộ idea của group CHƯA có reviewer
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           UPDATE Idea i
              SET i.reviewer = :teacher,
                  i.updatedAt = CURRENT_TIMESTAMP
            WHERE i.group = :group
              AND i.reviewer IS NULL
           """)
    int assignReviewerForGroupIfNull(@Param("group") Group group,
                                     @Param("teacher") User teacher);
    
    @Modifying
    @Transactional
    @Query("UPDATE Idea i SET i.active = false WHERE i.group.semester.id = :semesterId")
    void deactivateIdeasBySemester(@Param("semesterId") Long semesterId);

}
