package com.swd.exe.teammanagement.ai.router;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiContextBuilder {

    private final ObjectMapper objectMapper;

    /**
     * Convert object hoặc map sang JSON đẹp (pretty)
     */
    public String toJson(Object data) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot convert AI context to JSON", e);
        }
    }

    /**
     * Tạo đoạn instructions để Gemini hiểu rõ dữ liệu
     */
    public String wrapContext(Object data) {
        String json = toJson(data);
        return """
                Đây là dữ liệu JSON về nhóm của người dùng. 
                Hãy trả lời dựa trên dữ liệu này, không bịa thêm thông tin ngoài JSON.
                Không sử dụng Markdown, không dùng **bold**, *, _, hoặc emoji.
                
                DỮ LIỆU:
                """ + json;
    }
}
