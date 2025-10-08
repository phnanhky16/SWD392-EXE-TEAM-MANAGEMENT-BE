package com.swd.exe.teammanagement.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class SortUtil {
    private SortUtil() {}

    public static Sort sanitize(String sort, String dir, Set<String> whitelist,
                                String defaultField, Sort.Direction defaultDir) {
        // Nếu không có sort hoặc không nằm trong whitelist → dùng default
        if (sort == null || sort.isBlank() || !whitelist.contains(sort)) {
            return Sort.by(defaultDir, defaultField);
        }

        // Ngược lại → build sort từ FE
        return "desc".equalsIgnoreCase(dir)
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();
    }

    /**
     * Tạo Pageable từ page 1-based (FE thường truyền 1,2,3…)
     */
    public static Pageable pageable1Based(int page, int size, Sort sort) {
        page = (page <= 0) ? 1 : page;
        size = Math.min(Math.max(size, 1), 100); // tránh size quá lớn
        return PageRequest.of(page - 1, size, sort); // Spring Data dùng 0-based
    }
}
