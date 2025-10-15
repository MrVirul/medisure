package com.virul.medisure.controller;

import com.virul.medisure.policy.Policy;
import com.virul.medisure.policy.PolicyType;
import com.virul.medisure.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
@PreAuthorize("hasRole('POLICY_MANAGER')")
public class PolicyController {
    
    private final PolicyService policyService;
    
    @PostMapping
    public ResponseEntity<Policy> createPolicy(@RequestBody Policy policy) {
        try {
            Policy createdPolicy = policyService.createPolicy(policy);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        Optional<Policy> policy = policyService.getPolicyById(id);
        return policy.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<Page<Policy>> getAllPolicies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Policy> policies = policyService.getAllPolicies(pageable);
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/active/{isActive}")
    public ResponseEntity<Page<Policy>> getPoliciesByStatus(
            @PathVariable Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Policy> policies = policyService.getActivePolicies(isActive, pageable);
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/type/{policyType}")
    public ResponseEntity<Page<Policy>> getPoliciesByType(
            @PathVariable PolicyType policyType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Policy> policies = policyService.getPoliciesByType(policyType, pageable);
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/type/{policyType}/active/{isActive}")
    public ResponseEntity<Page<Policy>> getPoliciesByTypeAndStatus(
            @PathVariable PolicyType policyType,
            @PathVariable Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Policy> policies = policyService.getPoliciesByTypeAndStatus(policyType, isActive, pageable);
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<Policy>> searchPolicies(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Policy> policies = policyService.searchPolicies(query, pageable);
        return ResponseEntity.ok(policies);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Policy> updatePolicy(@PathVariable Long id, 
                                                     @RequestBody Policy policyDetails) {
        Optional<Policy> updatedPolicy = policyService.updatePolicy(id, policyDetails);
        return updatedPolicy.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        boolean deleted = policyService.deletePolicy(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}