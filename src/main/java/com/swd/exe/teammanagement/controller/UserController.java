package com.swd.exe.teammanagement.controller;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.UserUpdateRequest;
import com.swd.exe.teammanagement.dto.response.UserResponse;
import com.swd.exe.teammanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "User Management", description = "APIs for managing user profiles and information")
public class UserController {
      UserService userService;
      
      @Operation(
              summary = "Get user by ID", 
              description = "Retrieve a specific user's information by their unique identifier"
      )
      @GetMapping("/{id}")
      public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
          return ApiResponse.success("Get user successfully", userService.getUserById(id));
      }
      
      @Operation(
              summary = "Get all users",
              description = "Retrieve all users in the system. Typically used by admin or teachers."
      )
      @GetMapping("/")
      public ApiResponse<List<UserResponse>> getAllUsers() {
          return ApiResponse.success("Get all users successfully", userService.getAllUsers());
      }
      
      @Operation(
              summary = "Get my profile",
              description = "Retrieve the authenticated user's own profile information"
      )
      @GetMapping("/myInfo")
      public ApiResponse<UserResponse> getMyInfo() {
          return ApiResponse.success("Get my info successfully", userService.getMyInfo());
      }
      
      @Operation(
              summary = "Update user profile",
              description = "Update a user's profile information by ID. Requires appropriate permissions."
      )
      @PutMapping("/{id}")
      public ApiResponse<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request){
          return ApiResponse.success("Update user successfully", userService.updateUser(id, request));
      }
      
      @Operation(
              summary = "Update my profile",
              description = "Update the authenticated user's own profile information"
      )
      @PutMapping("/myInfo")
      public ApiResponse<UserResponse> updateMyInfo(@Valid @RequestBody UserUpdateRequest request){
          return ApiResponse.success("Update my info successfully", userService.updateMyInfo(request));
      }
      
      @Operation(
              summary = "Change user status",
              description = "Toggle user's active/inactive status. Typically used by admin to enable/disable user accounts."
      )
      @PatchMapping("/{id}")
      public ApiResponse<UserResponse> changeStatus(@PathVariable Long id) {
          return ApiResponse.success("Change status successfully", userService.changeStatus(id));
      }
}
