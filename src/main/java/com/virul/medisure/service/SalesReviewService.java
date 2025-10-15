package com.virul.medisure.service;

import com.virul.medisure.dto.PolicyApplicationReviewRequest;
import com.virul.medisure.dto.PolicyApplicationResponse;
import com.virul.medisure.repository.PolicyApplicationRepository;
import com.virul.medisure.sales.PolicyApplication;
import com.virul.medisure.sales.PolicyApplicationStatus;
import com.virul.medisure.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalesReviewService {
    
    private final PolicyApplicationRepository policyApplicationRepository;
    private final NotificationService notificationService;

    public Page<PolicyApplicationResponse> getPendingApplications(Pageable pageable) {
        return policyApplicationRepository.findByStatus(PolicyApplicationStatus.SUBMITTED, pageable)
                .map(this::mapToResponse);
    }
    
    public Page<PolicyApplicationResponse> getApplicationsByStatus(PolicyApplicationStatus status, Pageable pageable) {
        return policyApplicationRepository.findByStatus(status, pageable)
                .map(this::mapToResponse);
    }
    
    public Page<PolicyApplicationResponse> getApplicationsForReview(Pageable pageable) {
        // Get applications that are either SUBMITTED or UNDER_REVIEW
        return policyApplicationRepository.findByStatusIn(
                java.util.Arrays.asList(PolicyApplicationStatus.SUBMITTED, PolicyApplicationStatus.UNDER_REVIEW), 
                pageable)
                .map(this::mapToResponse);
    }
    
    public Optional<PolicyApplicationResponse> getApplicationById(Long id) {
        return policyApplicationRepository.findById(id).map(this::mapToResponse);
    }
    
    public Optional<PolicyApplicationResponse> reviewApplication(Long applicationId, 
                                                               PolicyApplicationReviewRequest reviewRequest) {
        return policyApplicationRepository.findById(applicationId)
                .map(application -> {
                    application.setStatus(reviewRequest.getStatus());
                    application.setReviewNotes(reviewRequest.getReviewNotes());
                    application.setReviewedBy(reviewRequest.getReviewedBy());
                    application.setReviewedAt(LocalDateTime.now());
                    
                    PolicyApplication updatedApplication = policyApplicationRepository.save(application);
                    
                    // Send notification to policy holder if status is MISSING_INFO
                    if (reviewRequest.getStatus() == PolicyApplicationStatus.MISSING_INFO) {
                        notificationService.sendMissingInfoNotification(application.getPolicyHolderId(), 
                                reviewRequest.getReviewNotes());
                    }
                    
                    return mapToResponse(updatedApplication);
                });
    }
    
    public Optional<PolicyApplicationResponse> requestAdditionalInfo(Long applicationId, 
                                                                   String reviewer, 
                                                                   String notes) {
        return policyApplicationRepository.findById(applicationId)
                .map(application -> {
                    application.setStatus(PolicyApplicationStatus.MISSING_INFO);
                    application.setReviewNotes(notes);
                    application.setReviewedBy(reviewer);
                    application.setReviewedAt(LocalDateTime.now());
                    
                    PolicyApplication updatedApplication = policyApplicationRepository.save(application);
                    
                    // Send notification to policy holder
                    notificationService.sendMissingInfoNotification(application.getPolicyHolderId(), notes);
                    
                    return mapToResponse(updatedApplication);
                });
    }
    
    public Optional<PolicyApplicationResponse> approveApplication(Long applicationId, String reviewer) {
        return policyApplicationRepository.findById(applicationId)
                .map(application -> {
                    application.setStatus(PolicyApplicationStatus.APPROVED);
                    application.setReviewedBy(reviewer);
                    application.setReviewedAt(LocalDateTime.now());
                    
                    PolicyApplication updatedApplication = policyApplicationRepository.save(application);
                    
                    // Send notification to policy holder
                    notificationService.sendApprovalNotification(application.getPolicyHolderId());
                    
                    return mapToResponse(updatedApplication);
                });
    }
    
    public Optional<PolicyApplicationResponse> rejectApplication(Long applicationId, 
                                                               String reviewer, 
                                                               String notes) {
        return policyApplicationRepository.findById(applicationId)
                .map(application -> {
                    application.setStatus(PolicyApplicationStatus.REJECTED);
                    application.setReviewNotes(notes);
                    application.setReviewedBy(reviewer);
                    application.setReviewedAt(LocalDateTime.now());
                    
                    PolicyApplication updatedApplication = policyApplicationRepository.save(application);
                    
                    // Send notification to policy holder
                    notificationService.sendRejectionNotification(application.getPolicyHolderId(), notes);
                    
                    return mapToResponse(updatedApplication);
                });
    }
    
    private PolicyApplicationResponse mapToResponse(PolicyApplication application) {
        PolicyApplicationResponse response = new PolicyApplicationResponse();
        response.setId(application.getId());
        response.setPolicyHolderId(application.getPolicyHolderId());
        response.setPolicyId(application.getPolicyId());
        response.setStatus(application.getStatus());
        response.setSubmittedDocuments(application.getSubmittedDocuments());
        response.setReviewNotes(application.getReviewNotes());
        response.setReviewedBy(application.getReviewedBy());
        response.setReviewedAt(application.getReviewedAt());
        response.setCreatedAt(application.getCreatedAt());
        response.setUpdatedAt(application.getUpdatedAt());
        return response;
    }
}