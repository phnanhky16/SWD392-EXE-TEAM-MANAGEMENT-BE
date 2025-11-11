package com.swd.exe.teammanagement.ai.router.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.swd.exe.teammanagement.ai.router.AiIntentHandler;
import com.swd.exe.teammanagement.ai.router.IntentExecutionResult;
import com.swd.exe.teammanagement.dto.response.GroupSummaryResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.GroupMember;
import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.exception.AppException;
import com.swd.exe.teammanagement.exception.ErrorCode;
import com.swd.exe.teammanagement.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CheckMyGroupStatusHandler implements AiIntentHandler {

    GroupMemberRepository groupMemberRepository;

    @Override
    public String intentName() {
        return "CHECK_MY_GROUP_STATUS";
    }

    @Override
    public IntentExecutionResult execute(User user, JsonNode args) {

        StringBuilder ctx = new StringBuilder();
        Map<String, Object> attachments = new HashMap<>();

        // 1) Lấy group hiện tại của user
        GroupMember gm = groupMemberRepository.findByUserAndActiveTrue(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        Group group = gm.getGroup();
        Long groupId = group.getId();

        // 2) Đếm số thành viên & số major khác nhau
        int memberCount = groupMemberRepository.countByGroupIdAndActiveTrue(groupId);
        List<Major> majors = groupMemberRepository.findMajorsByGroupId(groupId);
        Set<Major> distinctMajors = new HashSet<>(majors);
        int majorCount = distinctMajors.size();

        GroupStatus status = group.getStatus();

        // (Optional) Rule của bạn: đủ điều kiện LOCKED khi >=2 major & 1–6 thành viên
        boolean isDiverse = majorCount >= 2;
        boolean memberCountValid = memberCount >= 1 && memberCount <= 6;

        ctx.append("Thông tin nhóm hiện tại của người dùng:\n");
        ctx.append("- [GROUP] ID: ").append(groupId)
                .append(" | Tên: ").append(group.getTitle())
                .append(" | Trạng thái: ").append(status)
                .append("\n");
        ctx.append("- Số lượng thành viên hiện tại: ").append(memberCount).append("\n");
        ctx.append("- Số lượng ngành (major) khác nhau trong nhóm: ").append(majorCount).append("\n");
        ctx.append("- Vai trò của người dùng trong nhóm: ").append(gm.getMembershipRole()).append("\n");

        ctx.append("Đánh giá sơ bộ theo rule hệ thống:\n");
        ctx.append("- Đa dạng chuyên ngành (>=2 major): ").append(isDiverse ? "ĐẠT" : "CHƯA ĐẠT").append("\n");
        ctx.append("- Số lượng thành viên hợp lệ (1–6): ").append(memberCountValid ? "ĐẠT" : "CHƯA ĐẠT").append("\n");

        // DTO summary cho FE
        GroupSummaryResponse myGroupDto = new GroupSummaryResponse(groupId, group.getTitle());

        attachments.put("myGroup", myGroupDto);
        attachments.put("memberCount", memberCount);
        attachments.put("majorCount", majorCount);
        attachments.put("isDiverse", isDiverse);
        attachments.put("memberCountValid", memberCountValid);
        attachments.put("status", status);
        attachments.put("membershipRole", gm.getMembershipRole());

        return IntentExecutionResult.builder()
                .intent(intentName())
                .contextForPrompt(ctx.toString())
                .attachments(attachments)
                .rawResults(List.of(group))
                .build();
    }
}
