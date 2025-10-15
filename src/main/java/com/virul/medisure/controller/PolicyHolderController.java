package com.virul.medisure.controller;

import com.virul.medisure.dto.PolicyApplicationResponse;
import com.virul.medisure.policy.Policy;
import com.virul.medisure.service.PolicyService;
import com.virul.medisure.service.SalesReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
@PreAuthorize("hasRole('POLICY_HOLDER')")
public class PolicyHolderController {
    
    private final PolicyService policyService;
    private final SalesReviewService salesReviewService;
    
    @GetMapping("/available")
    public ResponseEntity<Page<Policy>> getAvailablePolicies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Get only active policies
        Page<Policy> policies = policyService.getActivePolicies(true, pageable);
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/applications")
    public ResponseEntity<Page<PolicyApplicationResponse>> getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // For demo purposes, returning empty page
        // In a real implementation, you would fetch applications for the current policy holder
        Page<PolicyApplicationResponse> applications = Page.empty();
        return ResponseEntity.ok(applications);
    }
}