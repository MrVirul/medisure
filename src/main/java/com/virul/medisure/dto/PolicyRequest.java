package com.virul.medisure.dto;

import com.virul.medisure.model.Policy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRequest {
    
    @NotBlank(message = "Policy name is required")
    private String name;
    
    @NotNull(message = "Policy type is required")
    private Policy.PolicyType type;
    
    @NotNull(message = "Coverage amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Coverage amount must be positive")
    private BigDecimal coverageAmount;
    
    @NotNull(message = "Premium amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Premium amount must be positive")
    private BigDecimal premiumAmount;
    
    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer durationMonths;
    
    private String description;
}
