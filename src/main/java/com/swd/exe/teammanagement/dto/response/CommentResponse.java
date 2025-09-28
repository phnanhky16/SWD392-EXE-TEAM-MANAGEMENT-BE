package com.swd.exe.teammanagement.dto.response;

import com.swd.exe.teammanagement.entity.Post;
import com.swd.exe.teammanagement.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    Long id;
    User user;
    Post post;
    String content;
    LocalDateTime createdAt;
}
