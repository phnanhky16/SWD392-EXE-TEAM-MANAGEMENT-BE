package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.entity.*;
import com.swd.exe.teammanagement.enums.idea_join_post_score.JoinStatus;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import com.swd.exe.teammanagement.enums.vote.ChoiceValue;
import com.swd.exe.teammanagement.enums.vote.VoteStatus;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.*;
import com.swd.exe.teammanagement.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class VoteServiceImpl implements VoteService {
    UserRepository userRepository;
    VoteRepository voteRepository;
    GroupRepository groupRepository;
    GroupMemberRepository groupMemberRepository;
    VoteChoiceRepository voteChoiceRepository;
    private final JoinRepository joinRepository;

    @Override
    public Vote voteJoin(Long groupId,Long userId) {
        User joinUser = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
        Group g = groupRepository.findById(groupId).orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
        return voteRepository.save(Vote.builder().group(g).topic("User "+ joinUser.getEmail()+" wants to join your group").status(VoteStatus.OPEN).targetUser(joinUser).closedAt(LocalDateTime.now().plusMinutes(6)).build());
    }
    @Override
    public VoteChoice voteChoice(Long voteId, ChoiceValue choiceValue) {
        Vote v = voteRepository.findById(voteId).orElseThrow(() -> new AppException(ErrorCode.VOTE_NOT_FOUND));
        User user = getCurrentUser();
        return voteChoiceRepository.save(VoteChoice.builder().choiceValue(choiceValue).vote(v).user(user).build());
    }

    @Override
    @Scheduled(fixedRate = 60000)
    public void voteDone(Long voteId) {
        Vote v = voteRepository.findById(voteId)
                .orElseThrow(() -> new AppException(ErrorCode.VOTE_NOT_FOUND));
        Group g = v.getGroup();
        List<User> members = groupMemberRepository.findUsersByGroup(g);
        List<VoteChoice> voteChoices = voteChoiceRepository.findByVote(v);
        if (members.size() == voteChoices.size()) {
            v.setStatus(VoteStatus.CLOSED);
            voteRepository.save(v);
            int total = 0;
            for (VoteChoice vc : voteChoices) {
                if (vc.getChoiceValue() == ChoiceValue.YES) {
                    total++;
                } else if (vc.getChoiceValue() == ChoiceValue.NO) {
                    total--;
                }
            }
            if (total >= 0) {
                groupMemberRepository.save(GroupMember.builder()
                        .group(g)
                        .user(v.getTargetUser())
                        .role(MembershipRole.MEMBER)
                        .build());
                joinRepository.save(Join.builder().toGroup(g).fromUser(v.getTargetUser()).status(JoinStatus.ACCEPTED).build());
            }else{
                joinRepository.save(Join.builder().toGroup(g).fromUser(v.getTargetUser()).status(JoinStatus.REJECTED).build());
            }
        } else if (v.getStatus()==VoteStatus.CLOSED) {
            int total = 0;
            int yes =members.size()-voteChoices.size();
            total+=yes;
            for (VoteChoice vc : voteChoices) {
                if (vc.getChoiceValue() == ChoiceValue.YES) {
                    total++;
                } else if (vc.getChoiceValue() == ChoiceValue.NO) {
                    total--;
                }
            }
            if (total >= 0) {
                groupMemberRepository.save(GroupMember.builder()
                        .group(g)
                        .user(v.getTargetUser())
                        .role(MembershipRole.MEMBER)
                        .build());
                joinRepository.save(Join.builder().toGroup(g).fromUser(v.getTargetUser()).status(JoinStatus.ACCEPTED).build());
            }else{
                joinRepository.save(Join.builder().toGroup(g).fromUser(v.getTargetUser()).status(JoinStatus.REJECTED).build());
            }
        }
    }

    @Override
    public List<Vote> getOpenVotes() {
        return voteRepository.findByStatus(VoteStatus.OPEN);
    }

    @Override
    public List<Vote> getVotesByGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
        return voteRepository.findByGroup(group);
    }

    @Override
    public Vote getVoteById(Long voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new AppException(ErrorCode.VOTE_NOT_FOUND));
    }

    @Override
    public List<VoteChoice> getVoteChoices(Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new AppException(ErrorCode.VOTE_NOT_FOUND));
        return voteChoiceRepository.findByVote(vote);
    }

    @Scheduled(fixedRate = 60000) // Mỗi phút kiểm tra
    public void autoCloseVotes() {
        List<Vote> openVotes = voteRepository.findByStatus(VoteStatus.OPEN);
        LocalDateTime now = LocalDateTime.now();

        for (Vote v : openVotes) {
            if (v.getClosedAt() != null && now.isAfter(v.getClosedAt())) {
                v.setStatus(VoteStatus.CLOSED);
                voteRepository.save(v);
                System.out.println("Vote ID " + v.getId() + " đã tự động đóng!");
            }
        }
    }
    private User getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
    }
}
