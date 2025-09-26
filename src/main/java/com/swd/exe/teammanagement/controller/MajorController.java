package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.MajorRequest;
import com.swd.exe.teammanagement.dto.response.MajorResponse;
import com.swd.exe.teammanagement.service.MajorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/majors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MajorController {
    MajorService majorService;
    @GetMapping("/{code}")
    public ApiResponse<MajorResponse> getMajorByCode(@PathVariable String code) {
        return ApiResponse.<MajorResponse>builder()
                .message("get major successfully")
                .result(majorService.getMajorByCode(code))
                .success(true)
                .build();
    }
    @GetMapping("/")
    public ApiResponse<List<MajorResponse>> getAllMajors() {
        return ApiResponse.<List<MajorResponse>>builder()
                .message("get all majors successfully")
                .result(majorService.getAllMajors())
                .success(true)
                .build();
    }
    @PostMapping("/")
    public ApiResponse<MajorResponse> createMajor(@RequestBody MajorRequest request) {
        return ApiResponse.<MajorResponse>builder()
                .message("create major successfully")
                .result(majorService.createMajor(request))
                .success(true)
                .build();
    }
    @PutMapping("/{code}")
    public ApiResponse<MajorResponse> updateMajor(@PathVariable String code, @RequestBody MajorRequest request) {
        return ApiResponse.<MajorResponse>builder()
                .message("update major successfully")
                .result(majorService.updateMajor(code, request))
                .success(true)
                .build();
    }
    @DeleteMapping("/{code}")
    public ApiResponse<Void> deleteMajor(@PathVariable String code) {
        return ApiResponse.<Void>builder().success(true).result(majorService.deleteMajor(code)).message("Delete major successfully").build();
    }
}
