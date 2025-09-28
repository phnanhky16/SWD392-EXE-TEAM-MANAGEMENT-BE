package com.swd.exe.teammanagement.mapper;

import com.swd.exe.teammanagement.dto.request.CommentRequest;
import com.swd.exe.teammanagement.dto.response.CommentResponse;
import com.swd.exe.teammanagement.entity.Comment;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentResponse toCommentResponse(Comment comment);
    Comment toComment(CommentRequest request);
    List<CommentResponse> toCommentResponseList(List<Comment> comments);
}
