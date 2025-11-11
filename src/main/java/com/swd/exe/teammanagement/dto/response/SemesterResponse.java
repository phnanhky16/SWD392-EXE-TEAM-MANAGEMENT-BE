package com.swd.exe.teammanagement.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SemesterResponse {
    Long id;
    String name;
    Boolean active;
    Boolean isComplete;
}
