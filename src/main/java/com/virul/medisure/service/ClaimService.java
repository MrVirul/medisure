package com.virul.medisure.service;

import com.virul.medisure.dto.ClaimRequest;
import com.virul.medisure.model.*;
import com.virul.medisure.repository.ClaimRepository;
import com.virul.medisure.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyHolderService policyHolderService;
    private final PolicyService policyService;
    private final AuditLogService auditLogService;

    public Claim submitClaim(Long policyHolderId, ClaimRequest request) {
        PolicyHolder policyHolder = policyHolderService.getPolicyHolderById(policyHolderId);
        Policy policy = policyService.getPolicyById(request.getPolicyId());

        // Verify policy holder owns this policy
        if (!policyHolder.getPolicy().getId().equals(request.getPolicyId())) {
            throw new RuntimeException("Policy holder does not own this policy");
        }

        Claim claim = new Claim();
        claim.setPolicyHolder(policyHolder);
        claim.setPolicy(policy);
        claim.setClaimDate(request.getClaimDate());
        claim.setAmountClaimed(request.getAmountClaimed());
        claim.setDescription(request.getDescription());
        claim.setMedicalDiagnosis(request.getMedicalDiagnosis());
        claim.setHospitalName(request.getHospitalName());
        claim.setTreatmentDate(request.getTreatmentDate());
        claim.setStatus(Claim.ClaimStatus.SUBMITTED);

        Claim savedClaim = claimRepository.save(claim);
        
        // Log the claim submission
        auditLogService.logAction(
            AuditLog.EntityType.CLAIM,
            savedClaim.getId(),
            AuditLog.Action.CREATE,
            policyHolder.getUser().getEmail(),
            "Claim submitted for amount: " + request.getAmountClaimed()
        );

        return savedClaim;
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public List<Claim> getClaimsByStatus(Claim.ClaimStatus status) {
        return claimRepository.findByStatus(status);
    }

    public List<Claim> getClaimsByPolicyHolder(Long policyHolderId) {
        PolicyHolder policyHolder = policyHolderService.getPolicyHolderById(policyHolderId);
        return claimRepository.findByPolicyHolder(policyHolder);
    }

    public Claim getClaimById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
    }

    public Claim reviewClaim(Long claimId, Claim.ClaimStatus status, String remarks) {
        Claim claim = getClaimById(claimId);
        claim.setStatus(status);
        claim.setRemarks(remarks);

        Claim savedClaim = claimRepository.save(claim);
        
        // Log the claim review
        auditLogService.logAction(
            AuditLog.EntityType.CLAIM,
            claimId,
            AuditLog.Action.UPDATE,
            "CLAIMS_MANAGER",
            "Claim reviewed with status: " + status + ". Remarks: " + remarks
        );

        return savedClaim;
    }

    public Claim forwardToFinance(Long claimId, String remarks) {
        Claim claim = getClaimById(claimId);
        claim.setStatus(Claim.ClaimStatus.FORWARDED_TO_FINANCE);
        claim.setRemarks(remarks);

        Claim savedClaim = claimRepository.save(claim);
        
        // Log the forward action
        auditLogService.logAction(
            AuditLog.EntityType.CLAIM,
            claimId,
            AuditLog.Action.FORWARD,
            "CLAIMS_MANAGER",
            "Claim forwarded to finance. Remarks: " + remarks
        );

        return savedClaim;
    }
}
