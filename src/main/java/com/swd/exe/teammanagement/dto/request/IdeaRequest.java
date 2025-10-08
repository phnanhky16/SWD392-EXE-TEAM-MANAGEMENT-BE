package com.swd.exe.teammanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IdeaRequest {
    @NotBlank
    @Size(min = 5, max = 100)
    String title;
    @Size(max=200)
    String description;
}
