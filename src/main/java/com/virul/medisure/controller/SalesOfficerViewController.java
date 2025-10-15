package com.virul.medisure.controller;

import com.virul.medisure.dto.PolicyApplicationResponse;
import com.virul.medisure.sales.PolicyApplication;
import com.virul.medisure.sales.PolicyApplicationStatus;
import com.virul.medisure.service.SalesReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sales-officer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SALES_OFFICER')")
public class SalesOfficerViewController {

    private final SalesReviewService salesReviewService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // For demo purposes, we'll use dummy data
        // In a real application, you would fetch actual statistics
        
        model.addAttribute("pendingCount", 12);
        model.addAttribute("approvedCount", 8);
        model.addAttribute("rejectedCount", 3);
        
        // Dummy recent applications data
        // In a real application, you would fetch actual recent applications
        /*
        Pageable pageable = PageRequest.of(0, 5);
        Page<PolicyApplicationResponse> recentApps = salesReviewService.getPendingApplications(pageable);
        model.addAttribute("recentApplications", recentApps.getContent());
        */
        
        return "sales-officer/dashboard";
    }

    @GetMapping("/applications")
    public String applicationsList(Model model) {
        return "sales-officer/applications";
    }

    @GetMapping("/applications/{id}")
    public String applicationDetail(@PathVariable Long id, Model model) {
        // In a real implementation, you would fetch the actual application
        // For now, we'll create a dummy application for demonstration
        PolicyApplicationResponse application = new PolicyApplicationResponse();
        application.setId(id);
        application.setPolicyHolderId(100L + id);
        application.setPolicyId(200L + id);
        application.setStatus(com.virul.medisure.sales.PolicyApplicationStatus.SUBMITTED);
        
        model.addAttribute("application", application);
        return "sales-officer/application-detail";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        // For now, redirect to dashboard
        return "redirect:/sales-officer/dashboard";
    }
}