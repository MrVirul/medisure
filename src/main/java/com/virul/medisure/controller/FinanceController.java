package com.virul.medisure.controller;

import com.virul.medisure.dto.ApiResponse;
import com.virul.medisure.model.FinanceRecord;
import com.virul.medisure.service.AuthService;
import com.virul.medisure.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER')")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;
    private final AuthService authService;

    @PostMapping("/process-claim/{claimId}")
    public ResponseEntity<ApiResponse<FinanceRecord>> processClaim(@PathVariable Long claimId, 
                                                                   @RequestBody Map<String, Object> request) {
        try {
            var user = authService.getCurrentUser();
            String statusStr = (String) request.get("status");
            String remarks = (String) request.get("remarks");
            Double approvedAmount = request.get("approvedAmount") != null ? 
                Double.parseDouble(request.get("approvedAmount").toString()) : 0.0;
            
            FinanceRecord.FinanceStatus status = FinanceRecord.FinanceStatus.valueOf(statusStr);
            FinanceRecord record = financeService.processClaim(claimId, user.getId(), status, remarks, approvedAmount);
            
            return ResponseEntity.ok(ApiResponse.success("Claim processed successfully", record));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/records")
    public ResponseEntity<ApiResponse<List<FinanceRecord>>> getAllFinanceRecords() {
        List<FinanceRecord> records = financeService.getAllFinanceRecords();
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/records/{id}")
    public ResponseEntity<ApiResponse<FinanceRecord>> getFinanceRecordById(@PathVariable Long id) {
        try {
            FinanceRecord record = financeService.getFinanceRecordById(id);
            return ResponseEntity.ok(ApiResponse.success(record));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/records/claim/{claimId}")
    public ResponseEntity<ApiResponse<List<FinanceRecord>>> getRecordsByClaim(@PathVariable Long claimId) {
        try {
            List<FinanceRecord> records = financeService.getFinanceRecordsByClaim(claimId);
            return ResponseEntity.ok(ApiResponse.success(records));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
