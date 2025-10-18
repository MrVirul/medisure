package com.virul.medisure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequest {
    
    @NotNull(message = "Policy ID is required")
    private Long policyId;
    
    @NotNull(message = "Claim date is required")
    @PastOrPresent(message = "Claim date cannot be in the future")
    private LocalDate claimDate;
    
    @NotNull(message = "Amount claimed is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount claimed must be positive")
    private BigDecimal amountClaimed;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private String medicalDiagnosis;
    
    private String hospitalName;
    
    private LocalDate treatmentDate;
}
