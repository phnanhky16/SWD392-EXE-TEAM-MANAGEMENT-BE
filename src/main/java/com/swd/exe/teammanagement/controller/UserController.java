package com.swd.exe.teammanagement.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.swd.exe.teammanagement.dto.ApiResponse;
import com.swd.exe.teammanagement.dto.request.UserUpdateRequest;
import com.swd.exe.teammanagement.dto.response.PagingResponse;
import com.swd.exe.teammanagement.dto.response.UserResponse;
import com.swd.exe.teammanagement.enums.user.UserRole;
import com.swd.exe.teammanagement.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/users")
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
      // ví dụ trong UserController
      @GetMapping
      public ApiResponse<PagingResponse<UserResponse>> getAllUsers(
              @Parameter(description = "Tìm kiếm theo tên, email, mã số sinh viên")
              @RequestParam(required = false) String q,
              @RequestParam(required = false) UserRole role,
              @RequestParam(required = false) Boolean active,
              @RequestParam(required = false) String majorCode,
              @RequestParam(defaultValue = "1")  int page,
              @RequestParam(defaultValue = "10") int size,
              @RequestParam(defaultValue = "id") String sort,
              @RequestParam(defaultValue = "desc") String dir
      ) {
          var data = userService.searchUsers(q, role, active, majorCode, page, size, sort, dir);
          return ApiResponse.success("List users successfully", data);
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

      @Operation(
              summary = "Update role for lecturer/moderator",
              description = "Chuyển đổi vai trò LECTURER <-> MODERATOR (chỉ admin có quyền thực hiện)"
      )
      @PatchMapping("/role/{id}")
      @PreAuthorize("hasRole('ADMIN')")
      public ApiResponse<UserResponse> updateRoleForLecturer(@PathVariable Long id) {
          return ApiResponse.success("Update role successfully", userService.updateRole(id));
      }
    @PostMapping("/{id}/avatar")
    public UserResponse uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile avatar) throws IOException {
        return userService.uploadAvatar(id, avatar);
    }

    @PostMapping("/{id}/cv")
    public UserResponse uploadCV(@PathVariable Long id, @RequestParam("file") MultipartFile cvFile) throws IOException {
        return userService.uploadCV(id, cvFile);
    }
    @GetMapping("/no-group")
    public ApiResponse<List<UserResponse>> getNoGroup() {
          return ApiResponse.success("Get User List without group successfully",userService.getUserNoGroup());
    }
    
    @Operation(
            summary = "Get users by semester and role",
            description = "Retrieve users (students, teachers, or moderators) by semester ID and role. Roles: STUDENT, LECTURER, MODERATOR"
    )
    @GetMapping("/semester/{semesterId}")
    public ApiResponse<List<UserResponse>> getUsersBySemesterAndRole(
            @PathVariable Long semesterId,
            @Parameter(description = "User role: STUDENT, LECTURER, or MODERATOR", required = true)
            @RequestParam UserRole role) {
        return ApiResponse.success("Get users by semester and role successfully", 
                                   userService.getUsersBySemesterAndRole(semesterId, role));
    }
}
