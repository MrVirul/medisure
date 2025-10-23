package com.virul.medisure.controller;

import com.virul.medisure.dto.ApiResponse;
import com.virul.medisure.model.Payment;
import com.virul.medisure.model.PolicyHolder;
import com.virul.medisure.service.AuthService;
import com.virul.medisure.service.PolicyHolderService;
import com.virul.medisure.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/policy-holder")
@RequiredArgsConstructor
public class PolicyHolderController {

    private final PolicyHolderService policyHolderService;
    private final AuthService authService;
    private final PdfService pdfService;

    @PostMapping("/purchase/{policyId}")
    @PreAuthorize("hasAnyRole('USER', 'POLICY_HOLDER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> purchasePolicy(@PathVariable Long policyId) {
        try {
            var user = authService.getCurrentUser();
            
            // Purchase the policy and process payment
            Map<String, Object> result = policyHolderService.purchasePolicyWithPayment(user.getId(), policyId);
            
            PolicyHolder policyHolder = (PolicyHolder) result.get("policyHolder");
            Payment payment = (Payment) result.get("payment");
            
            // Generate PDF document
            String pdfPath = pdfService.generatePolicyDocument(policyHolder);
            policyHolder.setPolicyDocumentUrl(pdfPath);
            
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("policyHolder", policyHolder);
            response.put("payment", payment);
            response.put("message", "Policy purchased successfully! Payment processed.");
            
            return ResponseEntity.ok(ApiResponse.success("Policy purchased successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-policy")
    @PreAuthorize("hasRole('POLICY_HOLDER')")
    public ResponseEntity<ApiResponse<PolicyHolder>> getMyPolicy() {
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderByUser(user);
            return ResponseEntity.ok(ApiResponse.success(policyHolder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'POLICY_MANAGER')")
    public ResponseEntity<ApiResponse<List<PolicyHolder>>> getAllPolicyHolders() {
        List<PolicyHolder> policyHolders = policyHolderService.getAllPolicyHolders();
        return ResponseEntity.ok(ApiResponse.success(policyHolders));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'POLICY_MANAGER')")
    public ResponseEntity<ApiResponse<PolicyHolder>> getPolicyHolderById(@PathVariable Long id) {
        try {
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderById(id);
            return ResponseEntity.ok(ApiResponse.success(policyHolder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'POLICY_MANAGER')")
    public ResponseEntity<ApiResponse<PolicyHolder>> updatePolicyStatus(@PathVariable Long id, 
                                                                        @RequestBody Map<String, String> request) {
        try {
            String statusStr = request.get("status");
            PolicyHolder.PolicyStatus status = PolicyHolder.PolicyStatus.valueOf(statusStr);
            PolicyHolder policyHolder = policyHolderService.updatePolicyStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Policy status updated successfully", policyHolder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
