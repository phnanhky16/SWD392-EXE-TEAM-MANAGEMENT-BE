package com.swd.exe.teammanagement.exception;


import com.swd.exe.teammanagement.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice // cho biet day la noi de create exception
@Slf4j
public class GlobalExceptionHandler {

    private static final String GENDER_ATTRIBUTE = "gender";

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(Exception exception) {

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getHttpStatusCode())
                .body(ApiResponse.builder().message(errorCode.getMessage()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        // Lấy lỗi đầu tiên từ danh sách FieldErrors
        FieldError fieldError = exception.getBindingResult().getFieldError();

        if (fieldError == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Validation error occurred", null, false));
        }

        // Lấy enum key từ DefaultMessage
        String enumKey = fieldError.getDefaultMessage();

        ErrorCode errorCode = ErrorCode.KEY_INVALID; // Default error code

        // Kiểm tra xem enumKey có trong ErrorCode không
        if (enumKey != null) {
            try {
                errorCode = ErrorCode.valueOf(enumKey);
            } catch (IllegalArgumentException e) {
                log.error(enumKey + " is not a valid ErrorCode");
            }
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    private String mapAttributes(String message, Map<String, Object> attributes) {
        String genderValue = attributes.get(GENDER_ATTRIBUTE).toString();

        return message.replace("{" + GENDER_ATTRIBUTE + "}", genderValue);
    }
}
