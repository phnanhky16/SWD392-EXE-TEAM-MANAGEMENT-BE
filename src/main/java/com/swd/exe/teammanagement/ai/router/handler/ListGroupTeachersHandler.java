package com.swd.exe.teammanagement.ai.router.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.swd.exe.teammanagement.ai.router.AiIntentHandler;
import com.swd.exe.teammanagement.ai.router.IntentExecutionResult;
import com.swd.exe.teammanagement.dto.response.GroupSummaryResponse;
import com.swd.exe.teammanagement.dto.response.UserSummaryResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.GroupTeacher;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.GroupRepository;
import com.swd.exe.teammanagement.repository.GroupMemberRepository;
import com.swd.exe.teammanagement.repository.GroupTeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ListGroupTeachersHandler implements AiIntentHandler {

    GroupRepository groupRepository;
    GroupMemberRepository groupMemberRepository;
    GroupTeacherRepository groupTeacherRepository;

    @Override
    public String intentName() {
        return "LIST_GROUP_TEACHERS";
    }

    @Override
    public IntentExecutionResult execute(User user, JsonNode args) {

        String scope = args.path("scope").asText("MY_GROUP");
        Group group;
        if ("BY_ID".equalsIgnoreCase(scope)) {
            if (!args.hasNonNull("groupId")) {
                throw new AppException(ErrorCode.INVALID_ARGUMENT);
            }
            Long groupId = args.get("groupId").asLong();
            group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
        } else {
            GroupMember membership = groupMemberRepository.findByUserAndActiveTrue(user)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
            group = membership.getGroup();
        }

        StringBuilder ctx = new StringBuilder();
        Map<String, Object> attachments = new HashMap<>();

        // 2) Lấy giáo viên đang phụ trách (nếu có)
        GroupTeacher gt = groupTeacherRepository.findByGroupAndActiveTrue(group)
                .orElse(null);

        ctx.append("Thông tin giáo viên phụ trách cho nhóm:\n");
        ctx.append("- [GROUP] ID: ").append(group.getId())
                .append(" | Tên: ").append(group.getTitle())
                .append("\n");

        if (gt == null) {
            ctx.append("- Nhóm hiện chưa có giáo viên phụ trách (hoặc giáo viên đã bị vô hiệu hóa).\n");
            return IntentExecutionResult.builder()
                    .intent(intentName())
                    .contextForPrompt(ctx.toString())
                    .attachments(attachments)
                    .rawResults(List.of())
                    .build();
        }

        User teacher = gt.getTeacher();

        ctx.append("Giáo viên phụ trách hiện tại:\n");
        ctx.append("- [TEACHER] ID: ").append(teacher.getId())
                .append(" | Tên: ").append(teacher.getFullName())
                .append(" | Email: ").append(teacher.getEmail())
                .append(" | Vai trò: ").append(teacher.getRole())
                .append("\n");

        // Build DTO cho FE
        UserSummaryResponse teacherDto = UserSummaryResponse.builder()
                .id(teacher.getId())
                .fullName(teacher.getFullName())
                .email(teacher.getEmail())
                .role(teacher.getRole())
                .build();

        attachments.put("teachers", List.of(teacherDto));
        attachments.put("group", new GroupSummaryResponse(group.getId(), group.getTitle()));

        return IntentExecutionResult.builder()
                .intent(intentName())
                .contextForPrompt(ctx.toString())
                .attachments(attachments)
                .rawResults(List.of(teacher))
                .build();
    }
}
