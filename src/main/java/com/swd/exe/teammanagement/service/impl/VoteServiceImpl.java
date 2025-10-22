package com.swd.exe.teammanagement.service.impl;

import com.swd.exe.teammanagement.entity.*;
import com.swd.exe.teammanagement.enums.idea_join_post_score.JoinStatus;
import com.swd.exe.teammanagement.enums.notification.NotificationStatus;
import com.swd.exe.teammanagement.enums.notification.NotificationType;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import com.swd.exe.teammanagement.enums.vote.ChoiceValue;
import com.swd.exe.teammanagement.enums.vote.VoteStatus;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.*;
import com.swd.exe.teammanagement.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    JoinRepository joinRepository;
    NotificationRepository notificationRepository;
    SimpMessagingTemplate messagingTemplate;

    // 🗳️ Tạo Vote cho yêu cầu Join nhóm
    @Override
    public Vote voteJoin(Long groupId, Long userId) {
        User joinUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

        Vote vote = Vote.builder()
                .group(group)
                .topic("User " + joinUser.getFullName() + " muốn tham gia nhóm " + group.getTitle())
                .status(VoteStatus.OPEN)
                .targetUser(joinUser)
                .closedAt(LocalDateTime.now().plusDays(1))
                .build();

        Vote savedVote = voteRepository.save(vote);

//        // 🛰️ Gửi thông báo real-time tới tất cả trong nhóm
//        messagingTemplate.convertAndSend("/topic/group/" + groupId,
//                "📢 Vote mới: " + savedVote.getTopic());

        return savedVote;
    }

    // ✅ Thành viên bầu chọn (YES / NO)
    @Override
    public VoteChoice voteChoice(Long voteId, ChoiceValue choiceValue) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new AppException(ErrorCode.VOTE_NOT_FOUND));

        User user = getCurrentUser();
        VoteChoice voteChoice = VoteChoice.builder()
                .choiceValue(choiceValue)
                .vote(vote)
                .user(user)
                .build();

        VoteChoice savedChoice = voteChoiceRepository.save(voteChoice);

//        // 🛰️ Thông báo real-time tới group
//        messagingTemplate.convertAndSend("/topic/group/" + vote.getGroup().getId(),
//                "🗳️ " + user.getFullName() + " đã vote " + choiceValue + " cho " + vote.getTopic());

        return savedChoice;
    }

    // ✅ Khi vote kết thúc (thủ công hoặc auto)
    @Override
    public void voteDone(Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new AppException(ErrorCode.VOTE_NOT_FOUND));

        processVoteResult(vote);
    }

    // 📊 Xử lý kết quả vote
    private void processVoteResult(Vote vote) {
        Group group = vote.getGroup();
        List<User> members = groupMemberRepository.findUsersByGroup(group);
        List<VoteChoice> voteChoices = voteChoiceRepository.findByVote(vote);

        int yes = (int) voteChoices.stream().filter(v -> v.getChoiceValue() == ChoiceValue.YES).count();
        int no = (int) voteChoices.stream().filter(v -> v.getChoiceValue() == ChoiceValue.NO).count();
        int total = yes - no;

        vote.setStatus(VoteStatus.CLOSED);
        voteRepository.save(vote);

        // ✅ Nếu YES nhiều hơn hoặc bằng NO → ACCEPTED
        if (total >= 0) {
            groupMemberRepository.save(GroupMember.builder()
                    .group(group)
                    .user(vote.getTargetUser())
                    .membershipRole(MembershipRole.MEMBER)
                    .build());

            joinRepository.save(Join.builder()
                    .toGroup(group)
                    .fromUser(vote.getTargetUser())
                    .status(JoinStatus.ACCEPTED)
                    .build());

//            // 🔔 Gửi notification cho người được chấp nhận
//            sendNotification(vote.getTargetUser(),
//                    "🎉 Bạn đã được chấp nhận vào nhóm " + group.getTitle(),
//                    NotificationType.JOIN_ACCEPTED);

//            // 🔔 Gửi notification cho các thành viên group
//            for (User member : members) {
//                if (!member.getId().equals(vote.getTargetUser().getId())) {
//                    sendNotification(member,
//                            "✅ " + vote.getTargetUser().getFullName() + " đã được chấp nhận vào nhóm " + group.getTitle(),
//                            NotificationType.JOIN_ACCEPTED);
//                }
//            }
//
//            // 🛰️ Gửi WebSocket thông báo tới group
//            messagingTemplate.convertAndSend("/topic/group/" + group.getId(),
//                    "✅ " + vote.getTargetUser().getFullName() + " đã được chấp nhận vào nhóm.");

        } else { // ❌ Bị từ chối
            joinRepository.save(Join.builder()
                    .toGroup(group)
                    .fromUser(vote.getTargetUser())
                    .status(JoinStatus.REJECTED)
                    .build());

//            sendNotification(vote.getTargetUser(),
//                    "❌ Yêu cầu tham gia nhóm " + group.getTitle() + " đã bị từ chối.",
//                    NotificationType.JOIN_REJECTED);
//
//            messagingTemplate.convertAndSend("/topic/group/" + group.getId(),
//                    "❌ " + vote.getTargetUser().getFullName() + " bị từ chối tham gia nhóm.");
        }
    }

    // 🕒 Tự động đóng vote mỗi phút
    @Scheduled(fixedRate = 60000)
    public void autoCloseVotes() {
        List<Vote> openVotes = voteRepository.findByStatus(VoteStatus.OPEN);
        LocalDateTime now = LocalDateTime.now();

        for (Vote vote : openVotes) {
            Group group = vote.getGroup();
            List<User> members = groupMemberRepository.findUsersByGroup(group);
            List<VoteChoice> voteChoices = voteChoiceRepository.findByVote(vote);

            int totalMembers = members.size();
            int totalVotes = voteChoices.size();

            boolean allVoted = totalVotes >= totalMembers;
            boolean timeExpired = vote.getClosedAt() != null && now.isAfter(vote.getClosedAt());

            // 🧠 Nếu tất cả thành viên đã vote HOẶC đã tới thời gian đóng
            if (allVoted || timeExpired) {
                vote.setStatus(VoteStatus.CLOSED);
                voteRepository.save(vote);

                try {
                    processVoteResult(vote);
                    System.out.println("Vote " + vote.getId() + " đã được auto xử lý "
                            + (allVoted ? "(đóng sớm do đủ lượt vote)" : "(đóng do hết hạn)"));
                } catch (Exception e) {
                    System.err.println("Lỗi xử lý vote " + vote.getId() + ": " + e.getMessage());
                }
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

    // 📩 helper
    private void sendNotification(User user, String content, NotificationType type) {
        notificationRepository.save(Notification.builder()
                .receiver(user)
                .content(content)
                .type(type)
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private User getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_UNEXISTED));
    }
}
