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

    private static final String CLAIM_DOCUMENTS_FOLDER = "claim-documents";

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

    @PostMapping("/submit-with-documents")
    @PreAuthorize("hasRole('POLICY_HOLDER')")
    public ResponseEntity<ApiResponse<Claim>> submitClaimWithDocuments(
            @RequestParam("policyId") Long policyId,
            @RequestParam("claimDate") String claimDate,
            @RequestParam("amountClaimed") String amountClaimed,
            @RequestParam("description") String description,
            @RequestParam(value = "medicalDiagnosis", required = false) String medicalDiagnosis,
            @RequestParam(value = "hospitalName", required = false) String hospitalName,
            @RequestParam(value = "treatmentDate", required = false) String treatmentDate,
            @RequestParam(value = "billDocument", required = false) MultipartFile billDocument,
            @RequestParam(value = "medicalReport", required = false) MultipartFile medicalReport,
            @RequestParam(value = "prescription", required = false) MultipartFile prescription,
            @RequestParam(value = "otherDocuments", required = false) List<MultipartFile> otherDocuments) {
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderByUser(user);
            
            ClaimRequest request = createClaimRequest(policyId, claimDate, amountClaimed, description, 
                    medicalDiagnosis, hospitalName, treatmentDate);
            
            Claim claim = claimService.submitClaim(policyHolder.getId(), request);
            
            uploadClaimDocuments(claim, billDocument, medicalReport, prescription, otherDocuments);
            
            return ResponseEntity.ok(ApiResponse.success("Claim submitted successfully with documents", claim));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private ClaimRequest createClaimRequest(Long policyId, String claimDate, String amountClaimed, 
                                           String description, String medicalDiagnosis, String hospitalName, 
                                           String treatmentDate) {
        ClaimRequest request = new ClaimRequest();
        request.setPolicyId(policyId);
        request.setClaimDate(java.time.LocalDate.parse(claimDate));
        request.setAmountClaimed(new java.math.BigDecimal(amountClaimed));
        request.setDescription(description);
        request.setMedicalDiagnosis(medicalDiagnosis);
        request.setHospitalName(hospitalName);
        if (treatmentDate != null && !treatmentDate.isEmpty()) {
            request.setTreatmentDate(java.time.LocalDate.parse(treatmentDate));
        }
        return request;
    }

    private void uploadClaimDocuments(Claim claim, MultipartFile billDocument, MultipartFile medicalReport,
                                     MultipartFile prescription, List<MultipartFile> otherDocuments) {
        if (billDocument != null && !billDocument.isEmpty()) {
            saveClaimDocument(claim, billDocument, ClaimDocument.DocumentType.BILL);
        }
        if (medicalReport != null && !medicalReport.isEmpty()) {
            saveClaimDocument(claim, medicalReport, ClaimDocument.DocumentType.MEDICAL_REPORT);
        }
        if (prescription != null && !prescription.isEmpty()) {
            saveClaimDocument(claim, prescription, ClaimDocument.DocumentType.PRESCRIPTION);
        }
        if (otherDocuments != null && !otherDocuments.isEmpty()) {
            for (MultipartFile document : otherDocuments) {
                if (!document.isEmpty()) {
                    saveClaimDocument(claim, document, ClaimDocument.DocumentType.OTHER);
                }
            }
        }
    }

    private void saveClaimDocument(Claim claim, MultipartFile file, ClaimDocument.DocumentType documentType) {
        String filePath = fileStorageService.storeFile(file, CLAIM_DOCUMENTS_FOLDER);
        ClaimDocument claimDoc = new ClaimDocument();
        claimDoc.setClaim(claim);
        claimDoc.setFileName(file.getOriginalFilename());
        claimDoc.setFileUrl(filePath);
        claimDoc.setFileType(file.getContentType());
        claimDoc.setFileSize(file.getSize());
        claimDoc.setDocumentType(documentType);
        claimDocumentRepository.save(claimDoc);
    }

    @PostMapping("/{claimId}/upload-document")
    @PreAuthorize("hasRole('POLICY_HOLDER')")
    public ResponseEntity<ApiResponse<ClaimDocument>> uploadClaimDocument(@PathVariable Long claimId,
                                                                          @RequestParam("file") MultipartFile file,
                                                                          @RequestParam("documentType") String documentType) {
        try {
            Claim claim = claimService.getClaimById(claimId);
            
            String filePath = fileStorageService.storeFile(file, CLAIM_DOCUMENTS_FOLDER);
            
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
