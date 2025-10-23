package com.virul.medisure.controller;

import com.virul.medisure.dto.ApiResponse;
import com.virul.medisure.model.Payment;
import com.virul.medisure.model.PolicyDocument;
import com.virul.medisure.model.PolicyHolder;
import com.virul.medisure.service.AuthService;
import com.virul.medisure.service.PolicyDocumentService;
import com.virul.medisure.service.PolicyHolderService;
import com.virul.medisure.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    private static final String REASON_PARAM = "reason";

    private final PolicyHolderService policyHolderService;
    private final AuthService authService;
    private final PdfService pdfService;
    private final PolicyDocumentService policyDocumentService;

    @GetMapping("/current-user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        try {
            var user = authService.getCurrentUser();
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("fullName", user.getFullName());
            userData.put("email", user.getEmail());
            userData.put("role", user.getRole().toString());
            return ResponseEntity.ok(ApiResponse.success(userData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

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
            
            // Save document to database
            String fileName = "Policy_Certificate_" + policyHolder.getId() + ".pdf";
            policyDocumentService.saveDocument(
                policyHolder,
                fileName,
                pdfPath,
                com.virul.medisure.model.PolicyDocument.DocumentType.POLICY_CERTIFICATE,
                "Policy certificate for " + policyHolder.getPolicy().getName()
            );
            
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

    @PostMapping("/my-policy/deactivate")
    @PreAuthorize("hasRole('POLICY_HOLDER')")
    public ResponseEntity<ApiResponse<PolicyHolder>> deactivateMyPolicy(@RequestBody Map<String, String> request) {
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderByUser(user);
            
            String reason = request.get(REASON_PARAM);
            if (reason == null || reason.trim().isEmpty()) {
                reason = "Policy holder requested cancellation";
            }
            
            PolicyHolder deactivated = policyHolderService.deactivatePolicy(
                policyHolder.getId(), 
                reason, 
                user.getEmail()
            );
            
            return ResponseEntity.ok(ApiResponse.success(
                "Your policy has been deactivated successfully. We're sorry to see you go!", 
                deactivated
            ));
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

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'POLICY_MANAGER')")
    public ResponseEntity<ApiResponse<PolicyHolder>> deactivatePolicy(@PathVariable Long id,
                                                                      @RequestBody Map<String, String> request) {
        try {
            var user = authService.getCurrentUser();
            String reason = request.get(REASON_PARAM);
            PolicyHolder policyHolder = policyHolderService.deactivatePolicy(id, reason, user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Policy deactivated successfully", policyHolder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasAnyRole('ADMIN', 'POLICY_MANAGER')")
    public ResponseEntity<ApiResponse<PolicyHolder>> suspendPolicy(@PathVariable Long id,
                                                                   @RequestBody Map<String, String> request) {
        try {
            var user = authService.getCurrentUser();
            String reason = request.get(REASON_PARAM);
            PolicyHolder policyHolder = policyHolderService.suspendPolicy(id, reason, user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Policy suspended successfully", policyHolder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/reactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'POLICY_MANAGER')")
    public ResponseEntity<ApiResponse<PolicyHolder>> reactivatePolicy(@PathVariable Long id) {
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.reactivatePolicy(id, user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Policy reactivated successfully", policyHolder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/deactivate-expired")
    @PreAuthorize("hasAnyRole('ADMIN', 'POLICY_MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deactivateExpiredPolicies() {
        try {
            var user = authService.getCurrentUser();
            List<PolicyHolder> expiredPolicies = policyHolderService.deactivateExpiredPolicies(user.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", expiredPolicies.size());
            response.put("expiredPolicies", expiredPolicies);
            
            return ResponseEntity.ok(ApiResponse.success(
                expiredPolicies.size() + " expired policies have been deactivated", 
                response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'POLICY_MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPolicyStatistics() {
        try {
            Map<PolicyHolder.PolicyStatus, Long> statusCounts = policyHolderService.getPolicyCountByStatus();
            long activeCount = policyHolderService.getActivePolicyCount();
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("activeCount", activeCount);
            statistics.put("statusCounts", statusCounts);
            statistics.put("totalPolicyHolders", policyHolderService.getAllPolicyHolders().size());
            
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-documents")
    @PreAuthorize("hasRole('POLICY_HOLDER')")
    public ResponseEntity<ApiResponse<List<PolicyDocument>>> getMyDocuments() {
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderByUser(user);
            List<PolicyDocument> documents = policyDocumentService.getDocumentsByPolicyHolder(policyHolder);
            return ResponseEntity.ok(ApiResponse.success(documents));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/documents/{documentId}/download")
    @PreAuthorize("hasRole('POLICY_HOLDER')")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderByUser(user);
            PolicyDocument document = policyDocumentService.getDocumentById(documentId);
            
            // Verify document belongs to current user
            if (!document.getPolicyHolder().getId().equals(policyHolder.getId())) {
                return ResponseEntity.status(403).build();
            }
            
            Resource resource = policyDocumentService.getDocumentAsResource(document);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + document.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{policyHolderId}/documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'POLICY_MANAGER')")
    public ResponseEntity<ApiResponse<List<PolicyDocument>>> getPolicyHolderDocuments(@PathVariable Long policyHolderId) {
        try {
            List<PolicyDocument> documents = policyDocumentService.getDocumentsByPolicyHolderId(policyHolderId);
            return ResponseEntity.ok(ApiResponse.success(documents));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/pending-approval")
    @PreAuthorize("hasAnyRole('ADMIN', 'POLICY_MANAGER', 'FINANCE_MANAGER')")
    public ResponseEntity<ApiResponse<List<PolicyHolder>>> getPendingApprovalPolicies() {
        try {
            List<PolicyHolder> pendingPolicies = policyHolderService.getPendingApprovalPolicies();
            return ResponseEntity.ok(ApiResponse.success(pendingPolicies));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/approve-policy-manager")
    @PreAuthorize("hasAnyRole('ADMIN', 'POLICY_MANAGER')")
    public ResponseEntity<ApiResponse<PolicyHolder>> approvePolicyByPolicyManager(@PathVariable Long id) {
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.approvePolicyByPolicyManager(id, user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Policy approved by Policy Manager successfully", policyHolder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/approve-finance-officer")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER')")
    public ResponseEntity<ApiResponse<PolicyHolder>> approvePolicyByFinanceOfficer(@PathVariable Long id) {
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.approvePolicyByFinanceOfficer(id, user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Policy approved by Finance Officer successfully", policyHolder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
