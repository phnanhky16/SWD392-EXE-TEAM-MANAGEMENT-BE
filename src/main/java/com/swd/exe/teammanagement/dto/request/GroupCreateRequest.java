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
public class GroupCreateRequest {
    @NotBlank(message = "INVALID_TITLE")
    @Size(min = 1, max = 200, message = "INVALID_TITLE")
    String title;
    
    @NotBlank(message = "INVALID_DESCRIPTION")
    @Size(min = 1, max = 1000, message = "INVALID_DESCRIPTION")
    String description;
}
