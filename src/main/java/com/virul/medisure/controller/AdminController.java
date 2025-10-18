package com.virul.medisure.controller;

import com.virul.medisure.dto.ApiResponse;
import com.virul.medisure.model.User;
import com.virul.medisure.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PostMapping("/create-employee")
    public ResponseEntity<ApiResponse<User>> createEmployee(@RequestBody Map<String, String> request) {
        try {
            String fullName = request.get("fullName");
            String email = request.get("email");
            String password = request.get("password");
            String phone = request.get("phone");
            User.UserRole role = User.UserRole.valueOf(request.get("role"));

            User user = userService.createEmployee(fullName, email, password, phone, role);
            return ResponseEntity.ok(ApiResponse.success("Employee created successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id,
                                                         @RequestBody Map<String, String> request) {
        try {
            String fullName = request.get("fullName");
            String email = request.get("email");
            String phone = request.get("phone");
            User.UserRole role = User.UserRole.valueOf(request.get("role"));

            User updatedUser = userService.updateUser(id, fullName, email, phone, role);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<Void>> changeUserRole(@PathVariable Long id, 
                                                            @RequestBody Map<String, String> request) {
        try {
            User.UserRole role = User.UserRole.valueOf(request.get("role"));
            userService.changeUserRole(id, role);
            return ResponseEntity.ok(ApiResponse.success("User role updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
