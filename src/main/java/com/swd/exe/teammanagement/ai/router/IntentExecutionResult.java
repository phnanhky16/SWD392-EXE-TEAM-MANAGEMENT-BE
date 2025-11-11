// com.swd.exe.teammanagement.ai.intent.IntentExecutionResult
package com.swd.exe.teammanagement.ai.router;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class IntentExecutionResult {

    /** Tên intent (cho chắc) */
    private String intent;

    /** Text mô tả dữ liệu để nhét vào prompt cho Gemini lần 2 */
    private String contextForPrompt;

    /** Dữ liệu “sống” trả về cho FE (groups, teachers, members, ...) */
    private Map<String, Object> attachments;

    /** (Optional) Nếu bạn vẫn muốn giữ raw objects, có thể dùng */
    private List<?> rawResults;
}
