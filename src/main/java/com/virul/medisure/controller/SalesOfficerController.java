package com.virul.medisure.controller;

import com.virul.medisure.dto.PolicyApplicationResponse;
import com.virul.medisure.dto.PolicyApplicationReviewRequest;
import com.virul.medisure.service.SalesReviewService;
import com.virul.medisure.sales.PolicyApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SALES_OFFICER')")
public class SalesOfficerController {

    private final SalesReviewService salesReviewService;

    @GetMapping("/applications/pending")
    public ResponseEntity<Page<PolicyApplicationResponse>> getPendingApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(salesReviewService.getPendingApplications(pageable));
    }

    @GetMapping("/applications/status/{status}")
    public ResponseEntity<Page<PolicyApplicationResponse>> getApplicationsByStatus(
            @PathVariable PolicyApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(salesReviewService.getApplicationsByStatus(status, pageable));
    }

    @GetMapping("/applications/{id}")
    public ResponseEntity<PolicyApplicationResponse> getApplicationById(@PathVariable Long id) {
        return salesReviewService.getApplicationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/applications/{applicationId}/review")
    public ResponseEntity<PolicyApplicationResponse> reviewApplication(
            @PathVariable Long applicationId,
            @RequestBody PolicyApplicationReviewRequest reviewRequest) {
        return salesReviewService.reviewApplication(applicationId, reviewRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/applications/{applicationId}/request-info")
    public ResponseEntity<PolicyApplicationResponse> requestAdditionalInfo(
            @PathVariable Long applicationId,
            @RequestParam String reviewer,
            @RequestParam String notes) {
        return salesReviewService.requestAdditionalInfo(applicationId, reviewer, notes)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/applications/{applicationId}/approve")
    public ResponseEntity<PolicyApplicationResponse> approveApplication(
            @PathVariable Long applicationId,
            @RequestParam String reviewer) {
        return salesReviewService.approveApplication(applicationId, reviewer)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/applications/{applicationId}/reject")
    public ResponseEntity<PolicyApplicationResponse> rejectApplication(
            @PathVariable Long applicationId,
            @RequestParam String reviewer,
            @RequestParam String notes) {
        return salesReviewService.rejectApplication(applicationId, reviewer, notes)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}