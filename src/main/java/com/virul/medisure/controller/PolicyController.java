package com.virul.medisure.controller;

import com.virul.medisure.dto.ApiResponse;
import com.virul.medisure.dto.PolicyRequest;
import com.virul.medisure.model.Policy;
import com.virul.medisure.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Policy>>> getAllPolicies() {
        List<Policy> policies = policyService.getActivePolicies();
        return ResponseEntity.ok(ApiResponse.success(policies));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'POLICY_MANAGER')")
    public ResponseEntity<ApiResponse<List<Policy>>> getAllPoliciesForManager() {
        List<Policy> policies = policyService.getAllPolicies();
        return ResponseEntity.ok(ApiResponse.success(policies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Policy>> getPolicyById(@PathVariable Long id) {
        try {
            Policy policy = policyService.getPolicyById(id);
            return ResponseEntity.ok(ApiResponse.success(policy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'POLICY_MANAGER')")
    public ResponseEntity<ApiResponse<Policy>> createPolicy(@Valid @RequestBody PolicyRequest request) {
        try {
            Policy policy = policyService.createPolicy(request);
            return ResponseEntity.ok(ApiResponse.success("Policy created successfully", policy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'POLICY_MANAGER')")
    public ResponseEntity<ApiResponse<Policy>> updatePolicy(@PathVariable Long id, 
                                                            @Valid @RequestBody PolicyRequest request) {
        try {
            Policy policy = policyService.updatePolicy(id, request);
            return ResponseEntity.ok(ApiResponse.success("Policy updated successfully", policy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'POLICY_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deletePolicy(@PathVariable Long id) {
        try {
            policyService.deletePolicy(id);
            return ResponseEntity.ok(ApiResponse.success("Policy deactivated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
