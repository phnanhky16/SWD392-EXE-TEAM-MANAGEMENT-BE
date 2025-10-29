package com.swd.exe.teammanagement.dto.request;

import com.swd.exe.teammanagement.enums.idea_join_post_score.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostRequest {
    @NotNull(message = "INVALID_POST_TYPE")
    PostType postType;
    
    @NotBlank(message = "INVALID_CONTENT")
    @Size(min = 1, max = 2000, message = "INVALID_CONTENT")
    String content;
}
