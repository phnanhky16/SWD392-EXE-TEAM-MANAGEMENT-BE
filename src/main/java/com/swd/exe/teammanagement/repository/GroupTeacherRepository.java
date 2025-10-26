package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupTeacher;
import com.swd.exe.teammanagement.entity.Semester;
import com.swd.exe.teammanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupTeacherRepository extends JpaRepository<GroupTeacher, Long> {
    long countByTeacherAndGroup_SemesterAndActiveTrue(User teacher, Semester groupSemester);

    Optional<GroupTeacher> findByGroupAndActiveTrue(Group group);

    boolean existsByGroupAndActiveTrue(Group group);
    
    List<GroupTeacher> findByActiveTrue();
    
    List<GroupTeacher> findByActiveFalse();
}
