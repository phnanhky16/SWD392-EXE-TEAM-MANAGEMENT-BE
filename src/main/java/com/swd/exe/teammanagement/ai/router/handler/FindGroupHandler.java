// com.swd.exe.teammanagement.ai.intent.FindGroupHandler
package com.swd.exe.teammanagement.ai.router.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.swd.exe.teammanagement.ai.router.AiIntentHandler;
import com.swd.exe.teammanagement.ai.router.IntentExecutionResult;
import com.swd.exe.teammanagement.dto.response.GroupSummaryResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.User;
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
public class FindGroupHandler implements AiIntentHandler {

    GroupRepository groupRepository;

    @Override
    public String intentName() {
        return "FIND_GROUP";
    }

    @Override
    public IntentExecutionResult execute(User user, JsonNode args) {
        String keyword = args.path("keyword").asText("");
        int limit = args.path("limit").asInt(5);

        List<Group> groups =
                groupRepository.searchActiveGroupsByKeywordFuzzy(
                        keyword
                );

        List<Group> limited = groups.stream()
                .limit(Math.max(1, limit))
                .toList();

        // Build context cho prompt lần 2
        StringBuilder ctx = new StringBuilder();
        ctx.append("KẾT QUẢ FIND_GROUP với keyword = ").append(keyword).append(":\n");
        if (limited.isEmpty()) {
            ctx.append("- Không có nhóm nào phù hợp.\n");
        } else {
            for (Group g : limited) {
                ctx.append("- [GROUP] ID: ").append(g.getId())
                        .append(" | Tên: ").append(g.getTitle())
                        .append(" | Mô tả: ")
                        .append(g.getDescription() == null ? "" : g.getDescription())
                        .append(" | Trạng thái: ").append(g.getStatus())
                        .append("\n");
            }
        }

        // Build attachments cho FE
        List<GroupSummaryResponse> groupSummaries = limited.stream()
                .map(g -> new GroupSummaryResponse(g.getId(), g.getTitle()))
                .collect(Collectors.toList());

        Map<String, Object> attachments = new HashMap<>();
        attachments.put("groups", groupSummaries);

        return IntentExecutionResult.builder()
                .intent(intentName())
                .contextForPrompt(ctx.toString())
                .attachments(attachments)
                .rawResults(limited)
                .build();
    }
}
