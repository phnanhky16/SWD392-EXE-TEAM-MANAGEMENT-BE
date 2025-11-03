package com.swd.exe.teammanagement.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.swd.exe.teammanagement.dto.request.SemesterRequest;
import com.swd.exe.teammanagement.dto.response.SemesterResponse;
import com.swd.exe.teammanagement.entity.Semester;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.mapper.SemesterMapper;
import com.swd.exe.teammanagement.repository.GroupInviteRepository;
import com.swd.exe.teammanagement.repository.GroupMemberRepository;
import com.swd.exe.teammanagement.repository.GroupRepository;
import com.swd.exe.teammanagement.repository.GroupTeacherRepository;
import com.swd.exe.teammanagement.repository.IdeaRepository;
import com.swd.exe.teammanagement.repository.JoinRepository;
import com.swd.exe.teammanagement.repository.PostRepository;
import com.swd.exe.teammanagement.repository.SemesterRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.repository.VoteChoiceRepository;
import com.swd.exe.teammanagement.repository.VoteRepository;
import com.swd.exe.teammanagement.service.SemesterService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class SemesterServiceImpl implements SemesterService {
    SemesterRepository semesterRepository;
    SemesterMapper semesterMapper;
    GroupRepository groupRepository;
    PostRepository postRepository;
    GroupMemberRepository groupMemberRepository;
    GroupTeacherRepository groupTeacherRepository;
    IdeaRepository ideaRepository;
    GroupInviteRepository groupInviteRepository;
    JoinRepository joinRepository;
    VoteRepository voteRepository;
    VoteChoiceRepository voteChoiceRepository;
    UserRepository userRepository;
    @Override
    @Transactional
    public SemesterResponse createSemester(SemesterRequest request) {
        if(semesterRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new AppException(ErrorCode.SEMESTER_EXISTED);
        }
        if(semesterRepository.existsByActive(true)){
            throw new AppException(ErrorCode.SEMESTER_JUST_ONE_ACTIVE);
        }
        Semester semester = Semester.builder()
                .name(request.getName())
                .active(true)
                .build();
        semesterRepository.save(semester);
        return semesterMapper.toSemesterResponse(semester);
    }

    @Override
    public SemesterResponse updateSemester(Long id, SemesterRequest request) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED));
        semesterMapper.toUpdateSemester(semester, request);
        semesterRepository.save(semester);
        return semesterMapper.toSemesterResponse(semester);
    }

    @Override
    public String changeActiveSemester(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED));
        if (Boolean.TRUE.equals(semester.getIsComplete())) {
            throw new AppException(ErrorCode.SEMESTER_ALREADY_COMPLETED);
        }
        semester.setActive(!Boolean.TRUE.equals(semester.getActive()));
        semesterRepository.save(semester);
        return "Semester active status changed successfully";
    }

    @Override
    public SemesterResponse getSemesterById(Long id) {
        return semesterMapper.toSemesterResponse(semesterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED)));
    }

    @Override
    public List<SemesterResponse> getAllSemesters() {
        return semesterMapper.toSemesterResponseList(semesterRepository.findAll());
    }

    @Override
    public SemesterResponse activateSemester(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED));
        if (Boolean.TRUE.equals(semester.getIsComplete())) {
            throw new AppException(ErrorCode.SEMESTER_ALREADY_COMPLETED);
        }
        semester.setActive(true);
        semesterRepository.save(semester);
        return semesterMapper.toSemesterResponse(semester);
    }

    @Override
    public SemesterResponse deactivateSemester(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED));
        if (Boolean.TRUE.equals(semester.getIsComplete())) {
            throw new AppException(ErrorCode.SEMESTER_ALREADY_COMPLETED);
        }
        semester.setActive(false);
        semesterRepository.save(semester);
        return semesterMapper.toSemesterResponse(semester);
    }

    @Override
    public SemesterResponse changeSemesterActiveStatus(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED));
        if (Boolean.TRUE.equals(semester.getIsComplete())) {
            throw new AppException(ErrorCode.SEMESTER_ALREADY_COMPLETED);
        }
        semester.setActive(!Boolean.TRUE.equals(semester.getActive()));
        semesterRepository.save(semester);
        return semesterMapper.toSemesterResponse(semester);
    }

    @Override
    @Transactional
    public String completeSemester(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_UNEXISTED));
        
        if (Boolean.TRUE.equals(semester.getIsComplete())) {
            throw new AppException(ErrorCode.SEMESTER_ALREADY_COMPLETED);
        }
        
        // Deactivate all Posts related to this semester
        postRepository.deactivatePostsBySemester(semester.getId());
        
        // Deactivate all Groups related to this semester
        groupRepository.deactivateGroupsBySemester(semester.getId());
        
        // Deactivate all GroupMembers in groups of this semester
        groupMemberRepository.deactivateGroupMembersBySemester(semester.getId());
        
        // Deactivate all GroupTeachers in groups of this semester
        groupTeacherRepository.deactivateGroupTeachersBySemester(semester.getId());
        
        // Deactivate all Ideas related to this semester
        ideaRepository.deactivateIdeasBySemester(semester.getId());
        
        // Deactivate all Invites related to this semester
        groupInviteRepository.deactivateInvitesBySemester(semester.getId());
        
        // Deactivate all Joins related to this semester
        joinRepository.deactivateJoinsBySemester(semester.getId());
        
        // Deactivate all Votes related to this semester
        voteRepository.deactivateVotesBySemester(semester.getId());
        
        // Deactivate all VoteChoices related to this semester
        voteChoiceRepository.deactivateVoteChoicesBySemester(semester.getId());
        
        // Deactivate all Users of this semester
        userRepository.deactivateUsersBySemesterId(semester.getId());
        
        // Mark semester as complete and inactive
        semester.setIsComplete(true);
        semester.setActive(false);
        semesterRepository.save(semester);
        
        return "Semester completed successfully. All related data has been deactivated.";
    }

}
