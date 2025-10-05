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
    IDEA_UNEXISTED("Idea does not exist", HttpStatus.NOT_FOUND),
    COMMENT_UNEXISTED("Comment does not exist", HttpStatus.BAD_REQUEST),
    ONLY_GROUP_LEADER("Only group leader can perform this action", HttpStatus.FORBIDDEN),
    ONLY_LEADER_CAN_MODIFY_IDEA("Only idea leader can modify this idea", HttpStatus.FORBIDDEN),
    ONLY_LEADER_CAN_SUBMIT("Only leader can submit the idea", HttpStatus.FORBIDDEN),
    ONLY_TEACHER_OR_ADMIN("Only teacher or admin can perform this action", HttpStatus.FORBIDDEN),
    GROUP_LOCKED("Group is locked", HttpStatus.BAD_REQUEST),
    GROUP_NOT_ACTIVE("Group is not active", HttpStatus.BAD_REQUEST),
    // Trạng thái hợp lệ theo vòng đời
    ONLY_DRAFT_OR_REJECTED_CAN_BE_DELETED("Only DRAFT or REJECT idea can be updated", HttpStatus.BAD_REQUEST),
    ONLY_DRAFT_OR_REJECTED_CAN_BE_UPDATED("Only DRAFT or REJECT idea can be deleted", HttpStatus.BAD_REQUEST),
    ONLY_DRAFT_OR_REJECTED_CAN_BE_SUBMITTED("Only DRAFT or REJECT idea can be submitted", HttpStatus.BAD_REQUEST),
    ONLY_PROPOSED_CAN_BE_APPROVED("Only PROPOSED idea can be approved", HttpStatus.BAD_REQUEST),
    ONLY_PROPOSED_CAN_BE_REJECTED("Only PROPOSED idea can be rejected", HttpStatus.BAD_REQUEST),
    CREATE_GROUP_NEED_INVITE_2_MEMBERS("Creating a group requires inviting at least 2 members", HttpStatus.BAD_REQUEST),
    INVITEE_MUST_BE_DISTINCT("The invitees must be different", HttpStatus.BAD_REQUEST),
    CANNOT_INVITE_CREATOR_AS_INVITEE("Cannot invite the creator as an invitee", HttpStatus.BAD_REQUEST),
    USER_ALREADY_IN_GROUP("User is already in a group", HttpStatus.BAD_REQUEST),
    USER_NOT_IN_GROUP("User is not in the group", HttpStatus.BAD_REQUEST),
    GROUP_SHOULD_ENOUGH_MEMBERS("Group should have enough members", HttpStatus.BAD_REQUEST),
    USER_NOT_LEADER_OF_ANY_GROUP("User is not a leader of any group", HttpStatus.BAD_REQUEST),
    JUST_ONE_POST_ONE_GROUP("Just one post is allowed for each group", HttpStatus.BAD_REQUEST),
    GROUP_LEADER_CANNOT_LEAVE("Group leader cannot leave the group", HttpStatus.BAD_REQUEST),
    ROLE_UPDATE_NOT_SWITCHABLE("Role update is only switchable between TEACHER and MODERATOR", HttpStatus.BAD_REQUEST),
    GROUP_NOT_FOUND("Group not found", HttpStatus.NOT_FOUND),
    ;

    private final String message;
    private final HttpStatusCode httpStatusCode;


    ErrorCode(String message, HttpStatusCode httpStatusCode) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
