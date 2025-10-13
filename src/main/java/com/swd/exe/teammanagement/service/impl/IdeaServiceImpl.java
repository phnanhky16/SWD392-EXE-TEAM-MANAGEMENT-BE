package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.dto.request.IdeaRequest;
import com.swd.exe.teammanagement.dto.response.IdeaResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.Idea;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.idea_join_post_score.IdeaSource;
import com.swd.exe.teammanagement.enums.idea_join_post_score.IdeaStatus;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import com.swd.exe.teammanagement.enums.user.UserRole;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.mapper.IdeaMapper;
import com.swd.exe.teammanagement.repository.GroupMemberRepository;
import com.swd.exe.teammanagement.repository.GroupRepository;
import com.swd.exe.teammanagement.repository.IdeaRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import com.swd.exe.teammanagement.service.IdeaService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class IdeaServiceImpl implements IdeaService {

    UserRepository userRepository;
    GroupRepository groupRepository;
    IdeaRepository ideaRepository;
    GroupMemberRepository groupMemberRepository;
    IdeaMapper ideaMapper;

    // Các tập trạng thái dùng lại nhiều nơi
    private static final EnumSet<IdeaStatus> EDITABLE = EnumSet.of(IdeaStatus.DRAFT, IdeaStatus.REJECTED);
    private static final EnumSet<IdeaStatus> SUBMITTABLE = EnumSet.of(IdeaStatus.DRAFT, IdeaStatus.REJECTED);

    // ========= CRUD =========

    @Override
    @Transactional
    public IdeaResponse createIdea(IdeaRequest request) {
        User current = getCurrentUser();

        Idea idea = ideaMapper.toIdea(request);   // map title, description
        idea.setAuthor(current);                  // entity mới dùng 'author'
        idea.setStatus(IdeaStatus.DRAFT);         // default

        if (isLecturerOrAdmin(current)) {
            // Ý tưởng tham khảo của giảng viên/ADMIN
            idea.setSource(IdeaSource.LECTURER);
        } else {
            // Ý tưởng của NHÓM do leader gửi — tự suy ra group theo leader hiện tại
            GroupMember gm = groupMemberRepository.findByUser(current)
                    .orElseThrow(() -> new AppException(ErrorCode.ONLY_GROUP_LEADER));
            if (gm.getMembershipRole() != MembershipRole.LEADER)
                throw new AppException(ErrorCode.ONLY_GROUP_LEADER);
            Group group = gm.getGroup();
            ensureGroupActiveForWrite(group);                 // chỉ check khi group != null
            ensureLeaderInGroup(current.getId(), group.getId()); // an toàn: xác thực đúng leader

            idea.setSource(IdeaSource.STUDENT);
            idea.setGroup(group);
        }

        return ideaMapper.toIdeaResponse(ideaRepository.save(idea));
    }


    @Override
    @Transactional(readOnly = true)
    public IdeaResponse getIdeaById(Long id) {
        return ideaMapper.toIdeaResponse(getIdeaOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdeaResponse> getAllIdeas() {
        return ideaMapper.toIdeaResponseList(ideaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdeaResponse> getAllIdeasByGroup(Long groupId) {
        return ideaMapper.toIdeaResponseList(ideaRepository.findAllByGroupIdOrderByCreatedAtDesc(groupId));
    }

    @Override
    @Transactional
    public IdeaResponse updateIdea(Long id, IdeaRequest request) {
        User current = getCurrentUser();
        Idea idea = getIdeaOrThrow(id);

        ensureGroupActiveForWrite(idea.getGroup());
        ensureLeaderOfIdea(current.getId(), idea);

        if (!EDITABLE.contains(idea.getStatus()))
            throw new AppException(ErrorCode.ONLY_DRAFT_OR_REJECTED_CAN_BE_UPDATED);

        ideaMapper.toUpdateIdea(idea, request);
        return ideaMapper.toIdeaResponse(ideaRepository.save(idea));
    }

    @Override
    @Transactional
    public Void deleteIdea(Long id) {
        User current = getCurrentUser();
        Idea idea = getIdeaOrThrow(id);

        ensureGroupActiveForWrite(idea.getGroup());
        ensureLeaderOfIdea(current.getId(), idea);

        if (!EDITABLE.contains(idea.getStatus()))
            throw new AppException(ErrorCode.ONLY_DRAFT_OR_REJECTED_CAN_BE_DELETED);

        ideaRepository.delete(idea);
        return null;
    }

    // ========= Lifecycle =========

    @Override
    @Transactional
    public IdeaResponse submitIdea(Long id) {
        User current = getCurrentUser();
        Idea idea = getIdeaOrThrow(id);

        ensureGroupActiveForWrite(idea.getGroup());
        ensureLeaderOfIdea(current.getId(), idea);

        if (!SUBMITTABLE.contains(idea.getStatus()))
            throw new AppException(ErrorCode.ONLY_DRAFT_OR_REJECTED_CAN_BE_SUBMITTED);

        idea.setStatus(IdeaStatus.PROPOSED);
        return ideaMapper.toIdeaResponse(ideaRepository.save(idea));
    }

    @Override
    @Transactional
    public IdeaResponse approveIdea(Long id) {
        User teacher = getCurrentUser();
        Idea idea = getIdeaOrThrow(id);

        ensureGroupActiveForWrite(idea.getGroup());
        ensureTeacherOrAdmin(teacher);

        if (idea.getStatus() != IdeaStatus.PROPOSED)
            throw new AppException(ErrorCode.ONLY_PROPOSED_CAN_BE_APPROVED);

        idea.setStatus(IdeaStatus.APPROVED);      // coi APPROVED là trạng thái đã public
        idea.setReviewer(teacher);
        idea.setCreatedAt(LocalDateTime.now());

        return ideaMapper.toIdeaResponse(ideaRepository.save(idea));
    }

    @Override
    @Transactional
    public IdeaResponse rejectIdea(Long id, String reason) {
        User teacher = getCurrentUser();
        Idea idea = getIdeaOrThrow(id);

        ensureGroupActiveForWrite(idea.getGroup());
        ensureTeacherOrAdmin(teacher);

        if (idea.getStatus() != IdeaStatus.PROPOSED)
            throw new AppException(ErrorCode.ONLY_PROPOSED_CAN_BE_REJECTED);

        idea.setStatus(IdeaStatus.REJECTED);
        idea.setReviewer(teacher);
        idea.setCreatedAt(LocalDateTime.now());
        idea.setReviewNote(reason);

        return ideaMapper.toIdeaResponse(ideaRepository.save(idea));
    }

    // ========= Helpers =========

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
    }

    private Idea getIdeaOrThrow(Long id) {
        return ideaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IDEA_UNEXISTED));
    }

    private void ensureLeaderInGroup(Long userId, Long groupId) {
        boolean ok = groupMemberRepository.existsByGroupIdAndUserIdAndMembershipRole(groupId, userId, MembershipRole.LEADER);
        if (!ok) throw new AppException(ErrorCode.ONLY_GROUP_LEADER);
    }

    private void ensureLeaderOfIdea(Long userId, Idea idea) {
        if (!Objects.equals(idea.getAuthor().getId(), userId))   // nếu entity dùng 'user' → idea.getUser().getId()
            throw new AppException(ErrorCode.ONLY_LEADER_CAN_MODIFY_IDEA);
    }

    private void ensureTeacherOrAdmin(User user) {
        UserRole role = user.getRole();
        if (!(role == UserRole.LECTURER || role == UserRole.ADMIN)) {
            throw new AppException(ErrorCode.ONLY_TEACHER_OR_ADMIN);
        }
    }

    private void ensureGroupActiveForWrite(Group group) {
        if (group.getStatus() == GroupStatus.LOCKED)
            throw new AppException(ErrorCode.GROUP_LOCKED);
        if (group.getStatus() != GroupStatus.ACTIVE)
            throw new AppException(ErrorCode.GROUP_NOT_ACTIVE);
    }
    private boolean isLecturerOrAdmin(User u) {
        return u.getRole() == UserRole.LECTURER || u.getRole() == UserRole.ADMIN;
    }
}
