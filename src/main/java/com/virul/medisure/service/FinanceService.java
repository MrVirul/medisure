package com.virul.medisure.service;

import com.virul.medisure.model.*;
import com.virul.medisure.repository.FinanceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final FinanceRecordRepository financeRecordRepository;
    private final ClaimService claimService;
    private final UserService userService;
    private final AuditLogService auditLogService;

    public FinanceRecord processClaim(Long claimId, Long financeManagerId, 
                                    FinanceRecord.FinanceStatus status, 
                                    String remarks, Double approvedAmount) {
        
        Claim claim = claimService.getClaimById(claimId);
        User financeManager = userService.getUserById(financeManagerId);

        FinanceRecord financeRecord = new FinanceRecord();
        financeRecord.setClaim(claim);
        financeRecord.setFinanceManager(financeManager);
        financeRecord.setStatus(status);
        financeRecord.setRemarks(remarks);
        financeRecord.setApprovedAmount(approvedAmount);

        FinanceRecord savedRecord = financeRecordRepository.save(financeRecord);
        
        // Update claim status
        if (status == FinanceRecord.FinanceStatus.APPROVED) {
            claim.setStatus(Claim.ClaimStatus.APPROVED_BY_FINANCE);
        } else if (status == FinanceRecord.FinanceStatus.REJECTED) {
            claim.setStatus(Claim.ClaimStatus.REJECTED);
        }
        claimService.reviewClaim(claimId, claim.getStatus(), remarks);
        
        // Log the finance action
        auditLogService.logAction(
            AuditLog.EntityType.FINANCE_RECORD,
            savedRecord.getId(),
            AuditLog.Action.CREATE,
            financeManager.getEmail(),
            "Claim processed with status: " + status + ". Approved amount: " + approvedAmount
        );

        return savedRecord;
    }

    public List<FinanceRecord> getAllFinanceRecords() {
        return financeRecordRepository.findAll();
    }

    public List<FinanceRecord> getFinanceRecordsByStatus(FinanceRecord.FinanceStatus status) {
        return financeRecordRepository.findByStatus(status);
    }

    public FinanceRecord getFinanceRecordById(Long id) {
        return financeRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Finance record not found"));
    }

    public List<FinanceRecord> getFinanceRecordsByClaim(Long claimId) {
        Claim claim = claimService.getClaimById(claimId);
        return financeRecordRepository.findByClaim(claim);
    }
}
