package com.swd.exe.teammanagement.ai.router.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.swd.exe.teammanagement.ai.router.AiContextBuilder;
import com.swd.exe.teammanagement.ai.router.AiIntentHandler;
import com.swd.exe.teammanagement.ai.router.IntentExecutionResult;
import com.swd.exe.teammanagement.dto.response.GroupSummaryResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.user.UserRole;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.GroupMemberRepository;
import com.swd.exe.teammanagement.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ListGroupDetailsHandler implements AiIntentHandler {

    GroupMemberRepository groupMemberRepository;
    GroupRepository groupRepository;
    AiContextBuilder aiContextBuilder;

    @Override
    public String intentName() {
        return "LIST_GROUP_DETAILS";
    }

    @Override
    public IntentExecutionResult execute(User user, JsonNode args) {

        // 1) Xác định scope & group
        String scope = args.path("scope").asText("MY_GROUP");

        Group group;
        Long groupId;

        if ("BY_ID".equalsIgnoreCase(scope)) {
            if (!args.hasNonNull("groupId")) {
                throw new AppException(ErrorCode.INVALID_ARGUMENT);
            }
            groupId = args.get("groupId").asLong();

            group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
        } else {
            GroupMember gm = groupMemberRepository.findByUserAndActiveTrue(user)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

            group = gm.getGroup();
            groupId = group.getId();
        }

        // 2) Lấy toàn bộ thành viên active của nhóm
        List<GroupMember> activeMembers = groupMemberRepository.findByGroupAndActiveTrue(group);

        // Danh sách giảng viên
        List<Map<String, Object>> teacherSummaries = activeMembers.stream()
                .filter(gm -> gm.getUser().getRole() == UserRole.LECTURER)
                .map(gm -> {
                    User u = gm.getUser();
                    Major m = u.getMajor();
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("name", u.getFullName());
                    map.put("email", u.getEmail());
                    map.put("hasMajor", m != null);
                    map.put("majorName", m != null ? m.getName() : null);
                    return map;
                })
                .collect(Collectors.toList());

        // Thông tin ngành của tất cả thành viên
        Set<String> distinctMajorNames = activeMembers.stream()
                .map(gm -> gm.getUser().getMajor())
                .filter(Objects::nonNull)
                .map(Major::getName)
                .collect(Collectors.toSet());

        int majorCount = distinctMajorNames.size();

        // Thông tin từng thành viên (cho AI đọc nếu cần)
        List<Map<String, Object>> memberSummaries = activeMembers.stream()
                .map(gm -> {
                    User u = gm.getUser();
                    Major m = u.getMajor();
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("name", u.getFullName());
                    map.put("email", u.getEmail());
                    map.put("userRole", u.getRole().name());
                    map.put("membershipRole", gm.getMembershipRole().name());
                    map.put("hasMajor", m != null);
                    map.put("majorName", m != null ? m.getName() : null);
                    return map;
                })
                .collect(Collectors.toList());

        GroupStatus status = group.getStatus();
        int memberCount = activeMembers.size();

        // 3) Build context JSON cho AI
        Map<String, Object> contextData = Map.of(
                "group", Map.of(
                        "title", group.getTitle(),
                        "status", status.name()
                ),
                "memberCount", memberCount,
                "majorCount", majorCount,
                "distinctMajors", distinctMajorNames,
                "teachers", teacherSummaries,
                "members", memberSummaries
        );

        String contextJson = aiContextBuilder.wrapContext(contextData);

        // 4) Attachments cho FE
        Map<String, Object> attachments = new HashMap<>();
        attachments.put("group", new GroupSummaryResponse(groupId, group.getTitle()));
        attachments.put("memberCount", memberCount);
        attachments.put("majorCount", majorCount);
        attachments.put("distinctMajors", distinctMajorNames);
        attachments.put("teachers", teacherSummaries);
        attachments.put("members", memberSummaries);
        attachments.put("status", status);

        return IntentExecutionResult.builder()
                .intent(intentName())
                .contextForPrompt(contextJson)
                .attachments(attachments)
                .rawResults(List.of(group))
                .build();
    }
}
