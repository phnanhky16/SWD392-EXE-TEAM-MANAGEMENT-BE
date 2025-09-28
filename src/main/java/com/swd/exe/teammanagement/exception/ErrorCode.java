package com.swd.exe.teammanagement.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION("Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    KEY_INVALID("Key is invalid", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_GG_TOKEN("Invalid Google token",HttpStatus.UNAUTHORIZED),
    USER_UNEXISTED("User does not exist", HttpStatus.BAD_REQUEST),
    MAJOR_EXISTED("Major already exists", HttpStatus.BAD_REQUEST),
    MAJOR_UNEXISTED("Major does not exist", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID_FORMAT("Email has invalid format", HttpStatus.BAD_REQUEST),
    GROUP_UNEXISTED("Group does not exist", HttpStatus.BAD_REQUEST),
    POST_UNEXISTED("Post does not exist", HttpStatus.BAD_REQUEST),
    DOES_NOT_DELETE_OTHER_USER_POST("You can not delete other user's post", HttpStatus.FORBIDDEN),
    COMMENT_UNEXISTED("Comment does not exist", HttpStatus.BAD_REQUEST)
    ;

    private final String message;
    private final HttpStatusCode httpStatusCode;

    ErrorCode(String message, HttpStatusCode httpStatusCode) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
