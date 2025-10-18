package com.virul.medisure.controller;

import com.virul.medisure.dto.ApiResponse;
import com.virul.medisure.dto.ClaimRequest;
import com.virul.medisure.model.Claim;
import com.virul.medisure.model.ClaimDocument;
import com.virul.medisure.model.PolicyHolder;
import com.virul.medisure.repository.ClaimDocumentRepository;
import com.virul.medisure.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;
    private final PolicyHolderService policyHolderService;
    private final AuthService authService;
    private final FileStorageService fileStorageService;
    private final ClaimDocumentRepository claimDocumentRepository;

    @PostMapping
    @PreAuthorize("hasRole('POLICY_HOLDER')")
    public ResponseEntity<ApiResponse<Claim>> submitClaim(@Valid @RequestBody ClaimRequest request) {
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderByUser(user);
            Claim claim = claimService.submitClaim(policyHolder.getId(), request);
            return ResponseEntity.ok(ApiResponse.success("Claim submitted successfully", claim));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{claimId}/upload-document")
    @PreAuthorize("hasRole('POLICY_HOLDER')")
    public ResponseEntity<ApiResponse<ClaimDocument>> uploadClaimDocument(@PathVariable Long claimId,
                                                                          @RequestParam("file") MultipartFile file,
                                                                          @RequestParam("documentType") String documentType) {
        try {
            Claim claim = claimService.getClaimById(claimId);
            
            // Store file
            String filePath = fileStorageService.storeFile(file, "claim-documents");
            
            // Create claim document record
            ClaimDocument claimDocument = new ClaimDocument();
            claimDocument.setClaim(claim);
            claimDocument.setFileName(file.getOriginalFilename());
            claimDocument.setFileUrl(filePath);
            claimDocument.setFileType(file.getContentType());
            claimDocument.setFileSize(file.getSize());
            claimDocument.setDocumentType(ClaimDocument.DocumentType.valueOf(documentType));
            
            claimDocumentRepository.save(claimDocument);
            
            return ResponseEntity.ok(ApiResponse.success("Document uploaded successfully", claimDocument));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-claims")
    @PreAuthorize("hasRole('POLICY_HOLDER')")
    public ResponseEntity<ApiResponse<List<Claim>>> getMyClaims() {
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderByUser(user);
            List<Claim> claims = claimService.getClaimsByPolicyHolder(policyHolder.getId());
            return ResponseEntity.ok(ApiResponse.success(claims));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLAIMS_MANAGER', 'FINANCE_MANAGER')")
    public ResponseEntity<ApiResponse<List<Claim>>> getAllClaims() {
        List<Claim> claims = claimService.getAllClaims();
        return ResponseEntity.ok(ApiResponse.success(claims));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLAIMS_MANAGER', 'FINANCE_MANAGER')")
    public ResponseEntity<ApiResponse<List<Claim>>> getClaimsByStatus(@PathVariable String status) {
        try {
            Claim.ClaimStatus claimStatus = Claim.ClaimStatus.valueOf(status);
            List<Claim> claims = claimService.getClaimsByStatus(claimStatus);
            return ResponseEntity.ok(ApiResponse.success(claims));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Claim>> getClaimById(@PathVariable Long id) {
        try {
            Claim claim = claimService.getClaimById(id);
            return ResponseEntity.ok(ApiResponse.success(claim));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLAIMS_MANAGER')")
    public ResponseEntity<ApiResponse<Claim>> reviewClaim(@PathVariable Long id, 
                                                          @RequestBody Map<String, String> request) {
        try {
            String statusStr = request.get("status");
            String remarks = request.get("remarks");
            Claim.ClaimStatus status = Claim.ClaimStatus.valueOf(statusStr);
            Claim claim = claimService.reviewClaim(id, status, remarks);
            return ResponseEntity.ok(ApiResponse.success("Claim reviewed successfully", claim));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/forward-to-finance")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLAIMS_MANAGER')")
    public ResponseEntity<ApiResponse<Claim>> forwardToFinance(@PathVariable Long id, 
                                                               @RequestBody Map<String, String> request) {
        try {
            String remarks = request.get("remarks");
            Claim claim = claimService.forwardToFinance(id, remarks);
            return ResponseEntity.ok(ApiResponse.success("Claim forwarded to finance", claim));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
