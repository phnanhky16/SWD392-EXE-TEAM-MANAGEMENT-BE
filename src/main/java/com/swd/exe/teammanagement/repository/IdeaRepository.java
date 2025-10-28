package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Idea;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.idea_join_post_score.IdeaStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IdeaRepository extends JpaRepository<Idea, Long> {

    List<Idea> findAllByGroupIdOrderByCreatedAtDesc(Long groupId);
    @Transactional
    void deleteIdeaByGroup(Group group);
    @Transactional
    void deleteIdeasByGroup(Group group);
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

}
