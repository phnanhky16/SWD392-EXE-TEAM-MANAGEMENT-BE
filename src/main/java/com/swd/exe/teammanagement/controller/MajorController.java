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
        return ApiResponse.success("Get major successfully", majorService.getMajorByCode(code));
    }
    @GetMapping("/")
    public ApiResponse<List<MajorResponse>> getAllMajors() {
        return ApiResponse.success("Get all majors successfully", majorService.getAllMajors());
    }
    @PostMapping("/")
    public ApiResponse<MajorResponse> createMajor(@RequestBody MajorRequest request) {
        return ApiResponse.created("Create major successfully", majorService.createMajor(request));
    }
    @PutMapping("/{code}")
    public ApiResponse<MajorResponse> updateMajor(@PathVariable String code, @RequestBody MajorRequest request) {
        return ApiResponse.success("Update major successfully", majorService.updateMajor(code, request));
    }
    @DeleteMapping("/{code}")
    public ApiResponse<Void> deleteMajor(@PathVariable String code) {
        majorService.deleteMajor(code);
        return ApiResponse.success("Delete major successfully", null);
    }
}
