package com.virul.medisure.dto;

import com.virul.medisure.sales.PolicyApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PolicyApplicationResponse {
    
    private Long id;
    
    private Long policyHolderId;
    
    private Long policyId;
    
    private PolicyApplicationStatus status;
    
    private String submittedDocuments;
    
    private String reviewNotes;
    
    private String reviewedBy;
    
    private LocalDateTime reviewedAt;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}