package com.virul.medisure.controller;

import com.virul.medisure.model.Policy;
import com.virul.medisure.model.PolicyHolder;
import com.virul.medisure.service.AuthService;
import com.virul.medisure.service.PolicyHolderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for policyholder-specific pages (Thymeleaf views)
 */
@Controller
@RequestMapping("/policyholder")
@RequiredArgsConstructor
public class PolicyHolderPageController {
    
    private final PolicyHolderService policyHolderService;
    private final AuthService authService;
    
    @GetMapping("/claims")
    public String claimsPage(Model model, HttpServletRequest request, Authentication authentication) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "My Claims");
        return "policyholder/claims";
    }
    
    @GetMapping("/claims/new")
    public String newClaimPage(Model model, HttpServletRequest request, Authentication authentication) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "File New Claim");
        return "policyholder/submit-claim";
    }
    
    @GetMapping("/appointments")
    public String appointmentsPage(Model model, HttpServletRequest request, Authentication authentication) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "My Appointments");
        
        // Check if user can book appointments
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderByUser(user);
            var policyType = policyHolder.getPolicy().getType();
            boolean canBookAppointments = (policyType == Policy.PolicyType.PREMIUM || policyType == Policy.PolicyType.SENIOR);
            model.addAttribute("canBookAppointments", canBookAppointments);
            model.addAttribute("policyType", policyType);
        } catch (Exception e) {
            model.addAttribute("canBookAppointments", false);
        }
        
        return "policyholder/appointments";
    }
    
    @GetMapping("/appointments/book")
    public String bookAppointmentPage(Model model, HttpServletRequest request, Authentication authentication, RedirectAttributes redirectAttributes) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "Book Appointment");
        
        // Validate that user has PREMIUM or SENIOR policy
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderByUser(user);
            var policyType = policyHolder.getPolicy().getType();
            
            if (policyType != Policy.PolicyType.PREMIUM && policyType != Policy.PolicyType.SENIOR) {
                redirectAttributes.addFlashAttribute("error", "Appointment booking is only available for PREMIUM and SENIOR policy holders. Please upgrade your policy to access this feature.");
                redirectAttributes.addFlashAttribute("policyType", policyType.toString());
                return "redirect:/policyholder/appointments";
            }
            
            model.addAttribute("canBookAppointments", true);
            model.addAttribute("policyType", policyType);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Unable to verify your policy. Please contact support.");
            return "redirect:/policyholder/dashboard";
        }
        
        return "policyholder/book-appointment";
    }
    
    @GetMapping("/policies")
    public String policiesPage(Model model, HttpServletRequest request, Authentication authentication) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "My Policies");
        return "policyholder/policies";
    }
    
    @GetMapping("/policies/buy")
    public String buyPolicyPage(Model model, HttpServletRequest request, Authentication authentication) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "Buy Policy");
        return "policyholder/buy-policy";
    }
    
    @GetMapping("/documents")
    public String documentsPage(Model model, HttpServletRequest request, Authentication authentication) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "My Documents");
        return "policyholder/documents";
    }
}

