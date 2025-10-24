package com.virul.medisure.controller;

import com.virul.medisure.dto.ApiResponse;
import com.virul.medisure.model.User;
import com.virul.medisure.service.AuthService;
import com.virul.medisure.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/users")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/users/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<User>> createEmployee(@RequestBody Map<String, Object> payload) {
        try {
            String fullName = extractString(payload, "fullName");
            String email = extractString(payload, "email");
            String password = extractString(payload, "password");
            String confirmPassword = extractString(payload, "confirmPassword");
            String phone = extractString(payload, "phone");
            String roleValue = extractString(payload, "role");

            if (fullName == null || fullName.isBlank()) {
                throw new IllegalArgumentException("Full name is required");
            }
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Password is required");
            }
            if (confirmPassword != null && !confirmPassword.equals(password)) {
                throw new IllegalArgumentException("Passwords do not match");
            }
            if (roleValue == null || roleValue.isBlank()) {
                throw new IllegalArgumentException("Role is required");
            }

            User.UserRole role = parseRole(roleValue);
            if (!isEmployeeRole(role)) {
                throw new IllegalArgumentException("Role " + role + " is not eligible for employee account creation");
            }
            User createdUser = userService.createEmployee(fullName, email, password, phone, role);
            return ResponseEntity.ok(ApiResponse.success(createdUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Unable to create employee account at this time"));
        }
    }

    private String extractString(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null) {
            return null;
        }
        return value.toString().trim();
    }

    private User.UserRole parseRole(String roleValue) {
        try {
            String sanitized = roleValue
                .trim()
                .toUpperCase()
                .replace(' ', '_');
            return User.UserRole.valueOf(sanitized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + roleValue);
        }
    }

    private boolean isEmployeeRole(User.UserRole role) {
        return role != User.UserRole.POLICY_HOLDER && role != User.UserRole.USER;
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id,
                                                        @RequestBody Map<String, Object> payload) {
        try {
            String fullName = extractString(payload, "fullName");
            String email = extractString(payload, "email");
            String phone = extractString(payload, "phone");
            String roleValue = extractString(payload, "role");

            if (fullName == null || fullName.isBlank()) {
                throw new IllegalArgumentException("Full name is required");
            }
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (roleValue == null || roleValue.isBlank()) {
                throw new IllegalArgumentException("Role is required");
            }

            User.UserRole role = parseRole(roleValue);
            if (!isEmployeeRole(role)) {
                throw new IllegalArgumentException("Role " + role + " is not eligible for employee account management");
            }

            User currentUser = null;
            try {
                currentUser = authService.getCurrentUser();
            } catch (Exception ignored) {
            }

            if (currentUser != null && currentUser.getId().equals(id) && role != User.UserRole.ADMIN) {
                throw new IllegalArgumentException("You cannot remove the ADMIN role from your own account");
            }

            User updatedUser = userService.updateUser(id, fullName, email, phone, role);
            return ResponseEntity.ok(ApiResponse.success("Employee updated", updatedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Unable to update employee account at this time"));
        }
    }

    /**
     * Change User Role - DISABLED
     * Admin should not be able to change user roles
     */
    // @PutMapping("/users/{id}/role")
    // public ResponseEntity<ApiResponse<Void>> changeUserRole(@PathVariable Long id, 
    //                                                         @RequestBody Map<String, String> request) {
    //     return ResponseEntity.status(403).body(ApiResponse.error("Role change is disabled for admin role"));
    // }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            User currentUser = null;
            try {
                currentUser = authService.getCurrentUser();
            } catch (Exception ignored) {
            }

            if (currentUser != null && currentUser.getId().equals(id)) {
                throw new IllegalArgumentException("You cannot delete your own account");
            }

            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("Employee deleted", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Unable to delete employee account at this time"));
        }
    }
}
