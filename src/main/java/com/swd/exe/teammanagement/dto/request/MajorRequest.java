package com.swd.exe.teammanagement.dto.request;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MajorRequest {
    @Column(nullable = false, unique = true)
    String name;
}
