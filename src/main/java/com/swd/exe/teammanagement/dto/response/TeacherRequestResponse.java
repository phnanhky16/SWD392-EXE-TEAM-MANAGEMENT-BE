// com.swd.exe.teammanagement.dto.response.TeacherRequestDetailResponse
package com.swd.exe.teammanagement.dto.response;

import com.swd.exe.teammanagement.enums.teacher.RequestStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TeacherRequestResponse {
    private Long requestId;
    private UserSummaryResponse teacher;
    private GroupSummaryResponse group;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    // Khi chưa có request
    private String message;
}
