package com.swd.exe.teammanagement.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.Semester;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;

public interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {

    @Query("SELECT gm.user FROM GroupMember gm WHERE gm.group = :group AND gm.membershipRole = 'LEADER'")
    Optional<User> findLeaderByGroup(@Param("group") Group group);


    List<Group> findGroupsByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);
    List<Group> findGroupsByStatusInAndCreatedAtBetween(Collection<GroupStatus> statuses, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    List<Group> findGroupsBySemesterAndActiveTrue(Semester semester);

    List<Group> findGroupsByStatusInAndSemesterAndActiveTrue(Collection<GroupStatus> statuses, Semester semester);

    long countBySemesterAndActiveTrue(Semester semester);

    List<Group> findByActiveTrue();

    List<Group> findByActiveFalse();

    @Query(value = """
        SELECT g.*
        FROM groups g
        WHERE g.active = true
          AND (
            unaccent(lower(g.title)) LIKE unaccent(lower(CONCAT('%', :keyword, '%')))
            OR unaccent(lower(COALESCE(g.description, ''))) LIKE unaccent(lower(CONCAT('%', :keyword, '%')))
          )
        """,
            nativeQuery = true)
    List<Group> searchActiveGroupsByKeywordFuzzy(@Param("keyword") String keyword);

    
    @Modifying
    @Query("UPDATE Group g SET g.active = false WHERE g.semester.id = :semesterId")
    void deactivateGroupsBySemester(@Param("semesterId") Long semesterId);
    
    List<Group> findAllBySemester(Semester semester);
}
