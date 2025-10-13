package com.swd.exe.teammanagement.dto.request;

import com.swd.exe.teammanagement.enums.idea_join_post_score.PostType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostRequest {
    PostType postType;
    String content;
}
