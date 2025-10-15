package com.virul.medisure.dto;

import com.virul.medisure.user.UserRole;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeUpdateRequest {
    
    private String username;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private String firstName;
    
    private String lastName;
    
    private String phoneNumber;
    
    private String employeeId;
    
    private String department;
    
    private LocalDate hireDate;
    
    private UserRole role;
    
    private Boolean active;
}