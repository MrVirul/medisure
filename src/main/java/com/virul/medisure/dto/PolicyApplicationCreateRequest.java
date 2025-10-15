package com.virul.medisure.dto;

import lombok.Data;

@Data
public class PolicyApplicationCreateRequest {
    
    private Long policyHolderId;
    
    private Long policyId;
    
    private String submittedDocuments;
}