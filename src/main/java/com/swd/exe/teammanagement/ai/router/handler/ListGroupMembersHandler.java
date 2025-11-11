// com.swd.exe.teammanagement.ai.intent.ListGroupMembersHandler
package com.swd.exe.teammanagement.ai.router.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.swd.exe.teammanagement.ai.router.AiIntentHandler;
import com.swd.exe.teammanagement.ai.router.IntentExecutionResult;
import com.swd.exe.teammanagement.dto.response.UserSummaryResponse;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ListGroupMembersHandler implements AiIntentHandler {

    GroupMemberRepository groupMemberRepository;

    @Override
    public String intentName() {
        return "LIST_GROUP_MEMBERS";
    }

    @Override
    public IntentExecutionResult execute(User user, JsonNode args) {
        Long groupId = args.hasNonNull("groupId") ? args.get("groupId").asLong() : null;
        if (groupId == null) {
            return IntentExecutionResult.builder()
                    .intent(intentName())
                    .contextForPrompt("Không có groupId hợp lệ được cung cấp.")
                    .attachments(Map.of())
                    .rawResults(List.of())
                    .build();
        }

        List<User> members = groupMemberRepository.findUsersByGroupId(groupId);

        StringBuilder ctx = new StringBuilder();
        ctx.append("Thành viên của nhóm ID = ").append(groupId).append(":\n");
        if (members.isEmpty()) {
            ctx.append("- Nhóm hiện chưa có thành viên.\n");
        } else {
            for (User m : members) {
                ctx.append("- [MEMBER] ID: ").append(m.getId())
                        .append(" | Tên: ").append(m.getFullName())
                        .append(" | Email: ").append(m.getEmail())
                        .append(" | Vai trò: ").append(m.getRole())
                        .append("\n");
            }
        }

        List<UserSummaryResponse> memberDtos = members.stream()
                .map(u -> UserSummaryResponse.builder()
                        .id(u.getId())
                        .fullName(u.getFullName())
                        .email(u.getEmail())
                        .role(u.getRole())
                        .build())
                .collect(Collectors.toList());

        Map<String, Object> attachments = new HashMap<>();
        attachments.put("members", memberDtos);

        return IntentExecutionResult.builder()
                .intent(intentName())
                .contextForPrompt(ctx.toString())
                .attachments(attachments)
                .rawResults(members)
                .build();
    }
}
