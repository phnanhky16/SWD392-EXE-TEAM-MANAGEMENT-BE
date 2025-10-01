package com.swd.exe.teammanagement.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupCreateRequest {
    String title;
    String description;
    List<String> inviteeEmails;
}
