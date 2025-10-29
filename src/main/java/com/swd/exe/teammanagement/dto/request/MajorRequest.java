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
public class MajorRequest {
    @NotBlank(message = "INVALID_MAJOR_NAME")
    @Size(min = 1, max = 100, message = "INVALID_MAJOR_NAME")
    String name;
}
