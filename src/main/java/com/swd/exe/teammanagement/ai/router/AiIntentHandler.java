// com.swd.exe.teammanagement.ai.intent.AiIntentHandler
package com.swd.exe.teammanagement.ai.router;

import com.fasterxml.jackson.databind.JsonNode;
import com.swd.exe.teammanagement.entity.User;

public interface AiIntentHandler {

    /**
     * Tên intent mà handler này xử lý, ví dụ:
     * - "FIND_GROUP"
     * - "LIST_GROUP_TEACHERS"
     */
    String intentName();

    /**
     * Thực thi intent với user hiện tại + args từ LLM.
     * Trả về kết quả để Orchestrator dùng cho:
     *  - build context cho Gemini lần 2
     *  - attachments trả về FE
     */
    IntentExecutionResult execute(User user, JsonNode args);
}
