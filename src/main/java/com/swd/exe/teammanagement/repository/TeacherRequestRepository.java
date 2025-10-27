package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.TeacherRequest;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.teacher.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRequestRepository extends JpaRepository<TeacherRequest, Long> {
    List<TeacherRequest> findByTeacherIdAndStatus(Long teacherId, RequestStatus status);

    List<TeacherRequest> findByTeacherAndStatus(User teacher, RequestStatus status);

    Optional<TeacherRequest> findTopByGroup_IdOrderByIdDesc(Long groupId);

}
