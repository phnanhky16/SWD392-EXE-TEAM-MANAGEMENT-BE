package com.swd.exe.teammanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InviteRequest {
    @NotNull
    Long groupId;

    @NotNull
    Long inviteeId;
}
