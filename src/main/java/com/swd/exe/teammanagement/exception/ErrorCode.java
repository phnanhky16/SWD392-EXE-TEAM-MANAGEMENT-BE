package com.swd.exe.teammanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION("Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    KEY_INVALID("Key is invalid", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_GG_TOKEN("Invalid Google token",HttpStatus.UNAUTHORIZED),
    USER_UNEXISTED("User does not exist", HttpStatus.BAD_REQUEST),
    MAJOR_EXISTED("Major already exists", HttpStatus.BAD_REQUEST),
    MAJOR_UNEXISTED("Major does not exist", HttpStatus.BAD_REQUEST),
    SEMESTER_EXISTED("Semester already exists", HttpStatus.BAD_REQUEST),
    SEMESTER_UNEXISTED("Semester does not exist", HttpStatus.BAD_REQUEST),
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
    JUST_ONE_POST_ONE_MEMBER("Just one post is allowed for each member", HttpStatus.BAD_REQUEST),
    VOTE_NOT_FOUND("Vote not found", HttpStatus.NOT_FOUND),
    JOIN_REQUEST_NOT_FOUND("Join request not found", HttpStatus.NOT_FOUND),
    JOIN_REQUEST_ALREADY_PROCESSED("Join request has already been processed", HttpStatus.BAD_REQUEST),
    CANNOT_TRANSFER_TO_SELF("Cannot transfer leadership to yourself", HttpStatus.BAD_REQUEST),
    GROUP_LEADER_NOT_FOUND("Group leader not found", HttpStatus.NOT_FOUND),
    DUPLICATE_VOTE("Duplicate vote", HttpStatus.BAD_REQUEST),
    DUPLICATE_JOIN_REQUEST("Duplicate join request", HttpStatus.BAD_REQUEST),
    NOTIFICATION_NOT_FOUND("Notification not found", HttpStatus.NOT_FOUND),
    GROUP_USER_NOT_SEMESTER_MATCH("student and the group must be in the same semester", HttpStatus.BAD_REQUEST),
    GROUP_NOT_DIVERSE("Group members must come from different majors", HttpStatus.BAD_REQUEST),
    GROUP_NOT_ENOUGH_MEMBER("Group must have at least 5 members", HttpStatus.BAD_REQUEST),
    TEACHER_OVERLOAD("Teacher has reached the maximum number of groups they can supervise", HttpStatus.BAD_REQUEST),
    TEACHER_ASSIGNED("Teacher has already been assigned to this group", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE("Invalid file type", HttpStatus.BAD_REQUEST),
    SEMESTER_NOT_ACTIVE("Semester is not active", HttpStatus.BAD_REQUEST),
    POST_OF_ANOTHER_GROUP("Post belongs to another group", HttpStatus.BAD_REQUEST),
    POST_ALREADY_ACTIVE("You already have an active post. Please deactivate it before creating a new one.",HttpStatus.BAD_REQUEST),
    UPDATE_MAJOR("you are forced to update major",HttpStatus.BAD_REQUEST),
    GROUP_UNLOCKED("Group is not locked", HttpStatus.BAD_REQUEST),
    REQUEST_UNEXISTED("Request does not exist", HttpStatus.BAD_REQUEST),
    REQUEST_ALREADY_RESPONDED("Request has already been responded to", HttpStatus.BAD_REQUEST),
    USER_NOT_TEACHER("User is not a teacher", HttpStatus.BAD_REQUEST)
    ;

    private final String message;
    private final HttpStatusCode httpStatusCode;


    ErrorCode(String message, HttpStatusCode httpStatusCode) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
