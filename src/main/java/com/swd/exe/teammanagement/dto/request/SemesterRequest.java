package com.swd.exe.teammanagement.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SemesterRequest {
    @NotBlank(message = "INVALID_SEMESTER_NAME")
    String name;
}
