package com.swd.exe.teammanagement.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION("Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    KEY_INVALID("Key is invalid", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("You do not have permission", HttpStatus.FORBIDDEN),
    USER_UNEXISTED("User does not exist", HttpStatus.BAD_REQUEST),
    MAJOR_EXISTED("Major already exists", HttpStatus.BAD_REQUEST),
    MAJOR_UNEXISTED("Major does not exist", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatusCode httpStatusCode;

    ErrorCode(String message, HttpStatusCode httpStatusCode) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
