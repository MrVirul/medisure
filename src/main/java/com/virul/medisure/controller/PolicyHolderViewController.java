package com.virul.medisure.controller;

import com.virul.medisure.dto.PolicyApplicationResponse;
import com.virul.medisure.sales.PolicyApplication;
import com.virul.medisure.sales.PolicyApplicationStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/policyholder")
public class PolicyHolderViewController {
    
    @GetMapping("/available-policies")
    public String availablePolicies() {
        return "policyholder/available-policies";
    }
    
    @GetMapping("/purchase-policy")
    public String purchasePolicy() {
        return "policyholder/purchase-policy";
    }
    
    @GetMapping("/my-applications")
    public String myApplications() {
        return "policyholder/my-applications";
    }
    
    @GetMapping("/application-details/{id}")
    public String applicationDetails(@PathVariable Long id, Model model) {
        // In a real implementation, you would fetch the actual application
        // For now, we'll create a dummy application for demonstration
        PolicyApplicationResponse application = new PolicyApplicationResponse();
        application.setId(id);
        application.setPolicyId(200L + id);
        application.setStatus(PolicyApplicationStatus.SUBMITTED);
        application.setSubmittedDocuments("document_" + id + ".pdf");
        
        model.addAttribute("application", application);
        return "policyholder/application-details";
    }
}