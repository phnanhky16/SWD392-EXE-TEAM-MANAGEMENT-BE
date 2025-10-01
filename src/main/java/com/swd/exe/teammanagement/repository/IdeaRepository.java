package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Idea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IdeaRepository extends JpaRepository<Idea, Long> {

    // ĐÃ dùng trong service:
    List<Idea> findAllByGroupIdOrderByCreatedAtDesc(Long groupId);

    // Cách 1 (khuyến nghị): truy cập thuộc tính lồng 'user.id'
    List<Idea> findAllByUser_Id(Long userId);

    void deleteIdeaByGroup(Group group);

    // Cách 2 (Spring cũng hiểu, nhưng ít rõ ràng hơn):
    // List<Idea> findAllByUserId(Long userId);
}
