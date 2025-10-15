package com.virul.medisure.dto;

import com.virul.medisure.sales.PolicyApplicationStatus;
import lombok.Data;

@Data
public class PolicyApplicationReviewRequest {
    
    private PolicyApplicationStatus status;
    
    private String reviewNotes;
    
    private String reviewedBy;
}