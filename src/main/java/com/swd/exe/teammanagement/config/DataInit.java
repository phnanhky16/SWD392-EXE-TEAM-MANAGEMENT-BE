package com.swd.exe.teammanagement.config;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;
import com.swd.exe.teammanagement.enums.user.MembershipRole;
import com.swd.exe.teammanagement.enums.user.UserRole;
import com.swd.exe.teammanagement.repository.GroupMemberRepository;
import com.swd.exe.teammanagement.repository.GroupRepository;
import com.swd.exe.teammanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@Profile("dev") // chỉ seed ở môi trường dev
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    // ===== Helpers để tránh lặp literal =====
    private static final String EMAIL_DOMAIN = "fpt.edu.vn";
    private static String email(int n) { return "student" + n + "@" + EMAIL_DOMAIN; }
    private static String groupTitle(int n) { return "Group EXE " + String.format("%02d", n); }
    private static String groupDesc(int n) { return "Nhóm EXE test " + String.format("%02d", n); }

    @Override
    public void run(String... args) {
        initUsers();
        initGroups();
        initGroupMembers();
    }

    private void initUsers() {
        if (userRepository.count() > 0) return;

        User student1 = User.builder().email(email(1)).fullName("Nguyễn Văn A")
                .role(UserRole.STUDENT).isActive(true).build();
        User student2 = User.builder().email(email(2)).fullName("Trần Thị B")
                .role(UserRole.STUDENT).isActive(true).build();
        User student3 = User.builder().email(email(3)).fullName("Nguyễn Văn C")
                .role(UserRole.STUDENT).isActive(true).build();
        User student4 = User.builder().email(email(4)).fullName("Nguyễn H")
                .role(UserRole.STUDENT).isActive(true).build();
        User student5 = User.builder().email(email(5)).fullName("Nguyễn D")
                .role(UserRole.STUDENT).isActive(true).build();
        User student6 = User.builder().email(email(6)).fullName("Trần Văn A")
                .role(UserRole.STUDENT).isActive(true).build();
        User student7 = User.builder().email(email(7)).fullName("Trần Văn B")
                .role(UserRole.STUDENT).isActive(true).build();

        userRepository.saveAll(List.of(student1, student2, student3, student4, student5, student6, student7));
    }

    private void initGroups() {
        if (groupRepository.count() > 0) return;

        User leader1 = userRepository.findByEmail(email(1)).orElseThrow();
        User leader2 = userRepository.findByEmail(email(5)).orElseThrow();
        User leader3 = userRepository.findByEmail(email(6)).orElseThrow();

        Group group1 = Group.builder()
                .title(groupTitle(1)).description(groupDesc(1))
                .status(GroupStatus.ACTIVE).type(GroupType.PUBLIC)
                .leader(leader1).createdAt(LocalDateTime.now()).build();

        Group group2 = Group.builder()
                .title(groupTitle(2)).description(groupDesc(2))
                .status(GroupStatus.ACTIVE).type(GroupType.PRIVATE)
                .leader(leader2).createdAt(LocalDateTime.now()).build();

        Group group3 = Group.builder()
                .title(groupTitle(3)).description(groupDesc(3))
                .status(GroupStatus.ACTIVE).type(GroupType.PUBLIC)
                .leader(leader3).createdAt(LocalDateTime.now()).build();

        groupRepository.saveAll(List.of(group1, group2, group3));
    }

    private void initGroupMembers() {
        if (groupMemberRepository.count() > 0) return;

        User student1 = userRepository.findByEmail(email(1)).orElseThrow();
        User student2 = userRepository.findByEmail(email(2)).orElseThrow();
        User student3 = userRepository.findByEmail(email(3)).orElseThrow();
        User student4 = userRepository.findByEmail(email(4)).orElseThrow();
        User student5 = userRepository.findByEmail(email(5)).orElseThrow();
        User student6 = userRepository.findByEmail(email(6)).orElseThrow();
        User student7 = userRepository.findByEmail(email(7)).orElseThrow();

        Group group1 = groupRepository.findByTitle(groupTitle(1)).orElseThrow();
        Group group2 = groupRepository.findByTitle(groupTitle(2)).orElseThrow();
        Group group3 = groupRepository.findByTitle(groupTitle(3)).orElseThrow();

        GroupMember gm1 = GroupMember.builder().user(student1).group(group1).role(MembershipRole.LEADER).build();
        GroupMember gm2 = GroupMember.builder().user(student2).group(group1).role(MembershipRole.MEMBER).build();
        GroupMember gm3 = GroupMember.builder().user(student3).group(group2).role(MembershipRole.MEMBER).build();
        GroupMember gm4 = GroupMember.builder().user(student4).group(group2).role(MembershipRole.MEMBER).build();
        GroupMember gm5 = GroupMember.builder().user(student5).group(group2).role(MembershipRole.LEADER).build();
        GroupMember gm6 = GroupMember.builder().user(student6).group(group3).role(MembershipRole.LEADER).build();
        GroupMember gm7 = GroupMember.builder().user(student7).group(group3).role(MembershipRole.MEMBER).build();

        groupMemberRepository.saveAll(List.of(gm1, gm2, gm3, gm4, gm5, gm6, gm7));
    }
}
