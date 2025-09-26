package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.UserUpdateRequest;
import com.swd.exe.teammanagement.dto.response.UserResponse;
import com.swd.exe.teammanagement.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
      UserService userService;
      @GetMapping("/{id}")
      public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
          return ApiResponse.<UserResponse>builder()
                  .message("Get user successfully")
                  .result(userService.getUserById(id))
                  .success(true).build();
      }
      @GetMapping("/")
      public ApiResponse<List<UserResponse>> getAllUsers() {
          return ApiResponse.<List<UserResponse>>builder()
                  .message("Get all users successfully")
                  .result(userService.getAllUsers())
                  .success(true).build();
      }
      @GetMapping("/myInfo")
      public ApiResponse<UserResponse> getMyInfo() {
          return ApiResponse.<UserResponse>builder()
                  .message("Get my info successfully")
                  .result(userService.getMyInfo())
                  .success(true)
                  .build();
      }
      @PutMapping("/{id}")
      public ApiResponse<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request){
          return ApiResponse.<UserResponse>builder()
                  .message("Update user successfully")
                  .result(userService.updateUser(id, request))
                  .success(true).build();
      }
      @PutMapping("/myInfo")
      public ApiResponse<UserResponse> updateMyInfo(@RequestBody UserUpdateRequest request){
          return ApiResponse.<UserResponse>builder()
                  .message("Update my info successfully")
                  .result(userService.updateMyInfo(request))
                  .success(true).build();
      }
      @PatchMapping("/{id}")
      public ApiResponse<UserResponse> changeStatus(@PathVariable Long id) {
      return ApiResponse.<UserResponse>builder()
              .result(userService.changeStatus(id))
              .success(true)
              .message("change status successfully")
              .build();
      }
}
