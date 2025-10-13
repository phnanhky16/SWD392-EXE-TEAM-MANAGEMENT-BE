package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Idea;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IdeaRepository extends JpaRepository<Idea, Long> {

    List<Idea> findAllByGroupIdOrderByCreatedAtDesc(Long groupId);
    @Transactional
    void deleteIdeaByGroup(Group group);
    @Transactional
    void deleteIdeasByGroup(Group group);
}
