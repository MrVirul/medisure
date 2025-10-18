package com.virul.medisure.dto;

import com.virul.medisure.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String email;
    private User.UserRole role;
    private String fullName;
    private Long userId;
    private String message;
    
    public AuthResponse(String token, User user) {
        this.token = token;
        this.email = user.getEmail();
        this.role = user.getRole();
        this.fullName = user.getFullName();
        this.userId = user.getId();
        this.message = "Login successful";
    }
}
