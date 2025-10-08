package com.swd.exe.teammanagement.dto.response;

import com.swd.exe.teammanagement.enums.user.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSummaryResponse {
    Long id;
    String fullName;
    String email;
    UserRole role;
}
