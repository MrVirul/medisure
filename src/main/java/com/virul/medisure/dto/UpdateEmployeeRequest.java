package com.virul.medisure.dto;

import com.virul.medisure.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form backing bean for updating employee accounts (admin use).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    @NotNull(message = "Role selection is required")
    private User.UserRole role;
}
