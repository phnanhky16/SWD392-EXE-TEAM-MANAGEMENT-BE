package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Idea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IdeaRepository extends JpaRepository<Idea, Long> {

    List<Idea> findAllByGroupIdOrderByCreatedAtDesc(Long groupId);
    void deleteIdeaByGroup(Group group);

}
