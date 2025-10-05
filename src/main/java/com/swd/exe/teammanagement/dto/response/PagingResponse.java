package com.swd.exe.teammanagement.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PagingResponse<T> {
    List<T> content;
    int page;            // 0-based
    int size;
    long totalElements;
    int totalPages;
    boolean first;
    boolean last;
    String sort;         // ví dụ "fullName,asc"
}
