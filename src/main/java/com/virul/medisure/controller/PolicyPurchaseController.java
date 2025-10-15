package com.virul.medisure.controller;

import com.virul.medisure.policy.PolicyPurchase;
import com.virul.medisure.policy.PolicyPurchaseStatus;
import com.virul.medisure.service.PolicyPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/policy-purchases")
@RequiredArgsConstructor
@PreAuthorize("hasRole('POLICY_MANAGER')")
public class PolicyPurchaseController {
    
    private final PolicyPurchaseService policyPurchaseService;
    
    @PostMapping
    public ResponseEntity<PolicyPurchase> createPolicyPurchase(@RequestBody PolicyPurchase policyPurchase) {
        try {
            PolicyPurchase createdPolicyPurchase = policyPurchaseService.createPolicyPurchase(policyPurchase);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicyPurchase);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PolicyPurchase> getPolicyPurchaseById(@PathVariable Long id) {
        Optional<PolicyPurchase> policyPurchase = policyPurchaseService.getPolicyPurchaseById(id);
        return policyPurchase.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<Page<PolicyPurchase>> getAllPolicyPurchases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PolicyPurchase> policyPurchases = policyPurchaseService.getAllPolicyPurchases(pageable);
        return ResponseEntity.ok(policyPurchases);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<PolicyPurchase>> getPolicyPurchasesByStatus(
            @PathVariable PolicyPurchaseStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PolicyPurchase> policyPurchases = policyPurchaseService.getPolicyPurchasesByStatus(status, pageable);
        return ResponseEntity.ok(policyPurchases);
    }
    
    @GetMapping("/policy-holder/{policyHolderId}")
    public ResponseEntity<Page<PolicyPurchase>> getPolicyPurchasesByPolicyHolder(
            @PathVariable Long policyHolderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PolicyPurchase> policyPurchases = policyPurchaseService.getPolicyPurchasesByPolicyHolder(policyHolderId, pageable);
        return ResponseEntity.ok(policyPurchases);
    }
    
    @GetMapping("/policy/{policyId}")
    public ResponseEntity<Page<PolicyPurchase>> getPolicyPurchasesByPolicy(
            @PathVariable Long policyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PolicyPurchase> policyPurchases = policyPurchaseService.getPolicyPurchasesByPolicy(policyId, pageable);
        return ResponseEntity.ok(policyPurchases);
    }
    
    @GetMapping("/expiring")
    public ResponseEntity<List<PolicyPurchase>> getExpiringPolicies(
            @RequestParam LocalDate date) {
        List<PolicyPurchase> expiringPolicies = policyPurchaseService.getExpiringPolicies(date);
        return ResponseEntity.ok(expiringPolicies);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PolicyPurchase> updatePolicyPurchase(@PathVariable Long id, 
                                                                     @RequestBody PolicyPurchase policyPurchaseDetails) {
        Optional<PolicyPurchase> updatedPolicyPurchase = policyPurchaseService.updatePolicyPurchase(id, policyPurchaseDetails);
        return updatedPolicyPurchase.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicyPurchase(@PathVariable Long id) {
        boolean deleted = policyPurchaseService.deletePolicyPurchase(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}