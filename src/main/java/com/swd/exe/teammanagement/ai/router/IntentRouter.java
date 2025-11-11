package com.swd.exe.teammanagement.ai.router;

import com.swd.exe.teammanagement.dto.response.GroupResponse;
import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.repository.GroupRepository;
import com.swd.exe.teammanagement.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Router cho các "hàm hệ thống" mà AI có thể gọi.
 * Với luồng hiện tại chúng ta quan tâm:
 *  - FIND_GROUP  -> tìm nhóm theo keyword
 *  - SUGGEST_GROUP -> gợi ý nhóm khả dụng (dùng luôn logic có sẵn trong GroupService)
 */
@Component
@RequiredArgsConstructor
public class IntentRouter {

    private final GroupRepository groupRepository;
    private final GroupService groupService;

    /**
     * @param intent  "FIND_GROUP" | "SUGGEST_GROUP" | ...
     * @param keyword từ khoá AI phân tích được (vd: "AI", "Java")
     * @param limit   số lượng kết quả tối đa
     * @return danh sách kết quả (thường là List<Group> hoặc List<GroupResponse>)
     */
    public List<?> route(String intent, String keyword, int limit) {

        // 1) Model gọi function find_groups -> tìm nhóm theo keyword trong DB
        if ("FIND_GROUP".equalsIgnoreCase(intent)) {

            List<Group> all = groupRepository.searchActiveGroupsByKeywordFuzzy(keyword);

            System.out.println("FIND_GROUP keyword = " + keyword + ", found = " + all.size());

            return all.stream()
                    .limit(Math.max(1, limit))
                    .collect(Collectors.toList());
        }


        // 2) Gợi ý nhóm cho sinh viên: dùng logic có sẵn trong GroupService
        if ("SUGGEST_GROUP".equalsIgnoreCase(intent)) {

            List<GroupResponse> available = groupService.getAvailableGroups();

            return available.stream()
                    .limit(Math.max(1, limit))
                    .collect(Collectors.toList());
        }

        // 3) UNKNOWN hoặc intent khác -> tạm thời chưa xử lý
        return Collections.emptyList();
    }
}
