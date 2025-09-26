package com.swd.exe.teammanagement.dto.response;

import com.swd.exe.teammanagement.entity.Major;
import com.swd.exe.teammanagement.enums.user.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String studentCode;
    String fullName;
    String email;
    String cvUrl;
    String avatarUrl;
    Major major;
    UserRole role;
    Boolean isActive;
}
