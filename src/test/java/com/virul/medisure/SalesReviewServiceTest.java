package com.virul.medisure;

import com.virul.medisure.dto.PolicyApplicationResponse;
import com.virul.medisure.repository.PolicyApplicationRepository;
import com.virul.medisure.sales.PolicyApplication;
import com.virul.medisure.sales.PolicyApplicationStatus;
import com.virul.medisure.service.NotificationService;
import com.virul.medisure.service.SalesReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SalesReviewServiceTest {

    @Mock
    private PolicyApplicationRepository policyApplicationRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SalesReviewService salesReviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPendingApplications() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        PolicyApplication application = new PolicyApplication();
        application.setId(1L);
        application.setPolicyHolderId(100L);
        application.setPolicyId(200L);
        application.setStatus(PolicyApplicationStatus.SUBMITTED);
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        Page<PolicyApplication> page = new PageImpl<>(Arrays.asList(application));
        when(policyApplicationRepository.findByStatus(PolicyApplicationStatus.SUBMITTED, pageable)).thenReturn(page);

        // When
        Page<PolicyApplicationResponse> result = salesReviewService.getPendingApplications(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        verify(policyApplicationRepository, times(1)).findByStatus(PolicyApplicationStatus.SUBMITTED, pageable);
    }

    @Test
    void testApproveApplication() {
        // Given
        Long applicationId = 1L;
        String reviewer = "testReviewer";

        PolicyApplication application = new PolicyApplication();
        application.setId(applicationId);
        application.setPolicyHolderId(100L);
        application.setPolicyId(200L);
        application.setStatus(PolicyApplicationStatus.SUBMITTED);
        application.setReviewedBy(reviewer);
        application.setReviewedAt(LocalDateTime.now());

        when(policyApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(policyApplicationRepository.save(any(PolicyApplication.class))).thenReturn(application);

        // When
        Optional<PolicyApplicationResponse> result = salesReviewService.approveApplication(applicationId, reviewer);

        // Then
        assertTrue(result.isPresent());
        assertEquals(PolicyApplicationStatus.APPROVED, result.get().getStatus());
        verify(policyApplicationRepository, times(1)).findById(applicationId);
        verify(policyApplicationRepository, times(1)).save(any(PolicyApplication.class));
        verify(notificationService, times(1)).sendApprovalNotification(100L);
    }

    @Test
    void testRejectApplication() {
        // Given
        Long applicationId = 1L;
        String reviewer = "testReviewer";
        String notes = "Rejected due to incomplete information";

        PolicyApplication application = new PolicyApplication();
        application.setId(applicationId);
        application.setPolicyHolderId(100L);
        application.setPolicyId(200L);
        application.setStatus(PolicyApplicationStatus.SUBMITTED);
        application.setReviewNotes(notes);
        application.setReviewedBy(reviewer);
        application.setReviewedAt(LocalDateTime.now());

        when(policyApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(policyApplicationRepository.save(any(PolicyApplication.class))).thenReturn(application);

        // When
        Optional<PolicyApplicationResponse> result = salesReviewService.rejectApplication(applicationId, reviewer, notes);

        // Then
        assertTrue(result.isPresent());
        assertEquals(PolicyApplicationStatus.REJECTED, result.get().getStatus());
        assertEquals(notes, result.get().getReviewNotes());
        verify(policyApplicationRepository, times(1)).findById(applicationId);
        verify(policyApplicationRepository, times(1)).save(any(PolicyApplication.class));
        verify(notificationService, times(1)).sendRejectionNotification(100L, notes);
    }
}