package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.UserUpdateRequest;
import com.swd.exe.teammanagement.dto.response.UserResponse;
import com.swd.exe.teammanagement.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
public class UserController {
      UserService userService;
      @GetMapping("/{id}")
      public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
          return ApiResponse.success("Get user successfully", userService.getUserById(id));
      }
      @GetMapping("/")
      public ApiResponse<List<UserResponse>> getAllUsers() {
          return ApiResponse.success("Get all users successfully", userService.getAllUsers());
      }
      @GetMapping("/myInfo")
      public ApiResponse<UserResponse> getMyInfo() {
          return ApiResponse.success("Get my info successfully", userService.getMyInfo());
      }
      @PutMapping("/{id}")
      public ApiResponse<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request){
          return ApiResponse.success("Update user successfully", userService.updateUser(id, request));
      }
      @PutMapping("/myInfo")
      public ApiResponse<UserResponse> updateMyInfo(@RequestBody UserUpdateRequest request){
          return ApiResponse.success("Update my info successfully", userService.updateMyInfo(request));
      }
      @PatchMapping("/{id}")
      public ApiResponse<UserResponse> changeStatus(@PathVariable Long id) {
          return ApiResponse.success("Change status successfully", userService.changeStatus(id));
      }
}
