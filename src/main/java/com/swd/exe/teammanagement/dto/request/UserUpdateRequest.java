package com.swd.exe.teammanagement.dto.request;

import com.swd.exe.teammanagement.enums.user.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.management.relation.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String cvUrl;
    String avatarUrl;
    String majorCode;
    UserRole role;
}
