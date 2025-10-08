package com.swd.exe.teammanagement.util;

import com.swd.exe.teammanagement.dto.response.PagingResponse;
import org.springframework.data.domain.Page;
import java.util.function.Function;

public final class PageUtil {
    private PageUtil() {}

    /**
     * Chuyển từ Page<Entity> sang PagingResponse<DTO>
     */
    public static <T, R> PagingResponse<R> toResponse(Page<T> page, Function<T, R> mapper) {
        var items = page.getContent().stream().map(mapper).toList();

        String sortStr = page.getSort().stream().findFirst()
                .map(o -> o.getProperty() + "," + o.getDirection().name().toLowerCase())
                .orElse(null);

        return PagingResponse.<R>builder()
                .content(items)
                .page(page.getNumber() + 1) // trả về 1-based
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .sort(sortStr)
                .build();
    }
}
