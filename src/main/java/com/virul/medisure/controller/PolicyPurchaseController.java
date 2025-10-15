package com.virul.medisure.controller;

import com.virul.medisure.policy.PolicyPurchase;
import com.virul.medisure.policy.PolicyPurchaseStatus;
import com.virul.medisure.service.PolicyPurchaseService;
import com.virul.medisure.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/policy-purchases")
@RequiredArgsConstructor
@PreAuthorize("hasRole('POLICY_MANAGER')")
public class PolicyPurchaseController {
    
    private final PolicyPurchaseService policyPurchaseService;
    private final FileUploadService fileUploadService;
    
    @PostMapping
    public ResponseEntity<PolicyPurchase> createPolicyPurchase(@RequestParam("policyPurchase") String policyPurchaseJson,
                                                              @RequestParam(value = "document", required = false) MultipartFile document) {
        try {
            // In a real implementation, you would parse the JSON and handle the document upload
            // For now, we'll just return a placeholder response
            return ResponseEntity.status(HttpStatus.CREATED).build();
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
    
    // New endpoints for policy holders
    @GetMapping("/available")
    @PreAuthorize("hasRole('POLICY_HOLDER')")
    public ResponseEntity<Page<com.virul.medisure.policy.Policy>> getAvailablePolicies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        // This would be implemented in a PolicyController in a real application
        // For now, we'll just return an empty page as a placeholder
        Pageable pageable = PageRequest.of(page, size);
        Page<com.virul.medisure.policy.Policy> emptyPage = Page.empty(pageable);
        return ResponseEntity.ok(emptyPage);
    }
    
    @PostMapping("/purchase")
    @PreAuthorize("hasRole('POLICY_HOLDER')")
    public ResponseEntity<String> purchasePolicy(
            @RequestParam("policyId") Long policyId,
            @RequestParam(value = "document", required = false) MultipartFile document) {
        
        try {
            // Handle document upload if provided
            String documentPath = null;
            if (document != null && !document.isEmpty()) {
                documentPath = fileUploadService.saveFile(document);
            }
            
            // In a real implementation, you would:
            // 1. Get the current policy holder from the security context
            // 2. Create a new PolicyPurchase with status PENDING
            // 3. Save the purchase
            // 4. Send a notification to the sales officer queue
            // 5. Return success response
            
            return ResponseEntity.ok("Purchase request submitted successfully. A sales officer will contact you shortly.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process purchase request: " + e.getMessage());
        }
    }
}