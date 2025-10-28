package com.swd.exe.teammanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponseString {
    int code;
    String message;
    String result;

    public static ApiResponseString success(String result) {
        return ApiResponseString.builder()
                .code(200)
                .message("OK")
                .result(result)
                .build();
    }

    public static ApiResponseString error(int code, String message) {
        return ApiResponseString.builder()
                .code(code)
                .message(message)
                .build();
    }
}
