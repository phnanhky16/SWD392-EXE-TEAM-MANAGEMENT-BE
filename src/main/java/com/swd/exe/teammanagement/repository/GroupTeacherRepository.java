package com.swd.exe.teammanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupTeacher;
import com.swd.exe.teammanagement.entity.Semester;
import com.swd.exe.teammanagement.entity.User;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GroupTeacherRepository extends JpaRepository<GroupTeacher, Long> {
    long countByTeacherAndGroup_SemesterAndActiveTrue(User teacher, Semester groupSemester);

    Optional<GroupTeacher> findByGroupAndActiveTrue(Group group);

    boolean existsByGroupAndActiveTrue(Group group);
    
    List<GroupTeacher> findByActiveTrue();
    
    List<GroupTeacher> findByActiveFalse();

    @Query("""
           select gt.group
           from GroupTeacher gt
           where gt.teacher = :teacher and gt.active = true
           order by gt.assignedAt desc, gt.id desc
           """)
    Page<Group> findActiveGroupsByTeacher(@Param("teacher") User teacher, Pageable pageable);

    // (tuỳ chọn) Lấy cả lịch sử (active true/false)
    @Query("""
           select gt.group
           from GroupTeacher gt
           where gt.teacher = :teacher
           order by gt.assignedAt desc, gt.id desc
           """)
    Page<Group> findAllGroupsByTeacher(@Param("teacher") User teacher, Pageable pageable);
    
    @Query("UPDATE GroupTeacher gt SET gt.active = false WHERE gt.group.semester.id = :semesterId")
    @Modifying
    @Transactional
    void deactivateGroupTeachersBySemester(@Param("semesterId") Long semesterId);
}
