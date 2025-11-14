// com.swd.exe.teammanagement.ai.intent.ListGroupMembersHandler
package com.swd.exe.teammanagement.ai.router.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.swd.exe.teammanagement.ai.router.AiIntentHandler;
import com.swd.exe.teammanagement.ai.router.IntentExecutionResult;
import com.swd.exe.teammanagement.dto.response.GroupSummaryResponse;
import com.swd.exe.teammanagement.dto.response.UserSummaryResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.GroupMemberRepository;
import com.swd.exe.teammanagement.repository.GroupRepository;
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
    GroupRepository groupRepository;

    @Override
    public String intentName() {
        return "LIST_GROUP_MEMBERS";
    }

    @Override
    public IntentExecutionResult execute(User user, JsonNode args) {
        String scope = args.path("scope").asText("MY_GROUP");
        Group targetGroup;

        if ("BY_ID".equalsIgnoreCase(scope)) {
            if (!args.hasNonNull("groupId")) {
                throw new AppException(ErrorCode.INVALID_ARGUMENT);
            }
            Long groupId = args.get("groupId").asLong();
            targetGroup = groupRepository.findById(groupId)
                    .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
        } else {
            GroupMember membership = groupMemberRepository.findByUserAndActiveTrue(user)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
            targetGroup = membership.getGroup();
        }

        List<GroupMember> activeMembers = groupMemberRepository.findByGroupAndActiveTrue(targetGroup);
        StringBuilder ctx = new StringBuilder();

        if (activeMembers.isEmpty()) {
            ctx.append("- Nhóm hiện chưa có thành viên.\n");
        } else {
            for (GroupMember gm : activeMembers) {
                User member = gm.getUser();
                ctx.append("- [MEMBER] ID: ").append(member.getId())
                        .append(" | Tên: ").append(member.getFullName())
                        .append(" | Email: ").append(member.getEmail())
                        .append(" | Vai trò: ").append(member.getRole())
                        .append(" | MembershipRole: ").append(gm.getMembershipRole())
                        .append("\n");
            }
        }

        List<UserSummaryResponse> memberDtos = activeMembers.stream()
                .map(gm -> {
                    User u = gm.getUser();
                    return UserSummaryResponse.builder()
                            .id(u.getId())
                            .fullName(u.getFullName())
                            .email(u.getEmail())
                            .role(u.getRole())
                            .build();
                })
                .collect(Collectors.toList());

        Map<String, Object> attachments = new HashMap<>();
        attachments.put("group", new GroupSummaryResponse(targetGroup.getId(), targetGroup.getTitle()));
        attachments.put("members", memberDtos);

        return IntentExecutionResult.builder()
                .intent(intentName())
                .contextForPrompt(ctx.toString())
                .attachments(attachments)
                .rawResults(activeMembers)
                .build();
    }
}
