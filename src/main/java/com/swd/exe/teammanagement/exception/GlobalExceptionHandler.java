package com.swd.exe.teammanagement.exception;

import com.swd.exe.teammanagement.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // cho biet day la noi de create exception
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Object>> handlingRuntimeException(Exception exception) {
        log.error("Unexpected Exception: {}", exception.getMessage(), exception);
        
        ApiResponse<Object> apiResponse = ApiResponse.error(500, ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.status(500).body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Object>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        log.error("App Exception: {}", errorCode.getMessage(), exception);

        int statusCode = errorCode.getHttpStatusCode().value();
        ApiResponse<Object> apiResponse = ApiResponse.error(statusCode, errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Object>> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        log.error("Access Denied Exception: {}", errorCode.getMessage(), exception);

        int statusCode = errorCode.getHttpStatusCode().value();
        return ResponseEntity.status(errorCode.getHttpStatusCode())
                .body(ApiResponse.error(statusCode, errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handlingValidation(MethodArgumentNotValidException exception) {
        log.error("Validation Exception: {}", exception.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> apiResponse = ApiResponse.<Map<String, String>>builder()
            .status(400)
            .message("Validation failed")
            .data(errors)
            .build();

        return ResponseEntity.status(400).body(apiResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("Illegal Argument Exception: {}", exception.getMessage(), exception);
        
        ApiResponse<Object> response = ApiResponse.error(400, exception.getMessage());
        return ResponseEntity.status(400).body(response);
    }
}
