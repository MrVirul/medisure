package com.virul.medisure.controller;

import com.virul.medisure.model.*;
import com.virul.medisure.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Controller for dashboard pages with real-time data from database
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {
    
    private final PolicyHolderRepository policyHolderRepository;
    private final ClaimRepository claimRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PolicyRepository policyRepository;
    
    /**
     * Redirect to appropriate dashboard based on user role
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/auth/login";
        }
        
        // Check user role and redirect accordingly
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_POLICY_HOLDER"))) {
            return "redirect:/policyholder/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            // USER role should also go to policyholder dashboard
            return "redirect:/policyholder/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DOCTOR"))) {
            return "redirect:/doctor/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SALES_OFFICER"))) {
            return "redirect:/agent/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OPERATION_MANAGER")) ||
                   authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_POLICY_MANAGER")) ||
                   authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLAIMS_MANAGER")) ||
                   authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_FINANCE_MANAGER")) ||
                   authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MEDICAL_COORDINATOR")) ||
                   authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER_SUPPORT_OFFICER"))) {
            return "redirect:/admin/dashboard";
        }
        
        // Default to login if no matching role
        return "redirect:/auth/login";
    }
    
    /**
     * Admin Dashboard with real-time data
     */
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "Admin Dashboard");
        
        // Total active policy holders
        long totalPolicies = policyHolderRepository.findByStatus(PolicyHolder.PolicyStatus.ACTIVE).size();
        model.addAttribute("totalPolicies", totalPolicies);
        
        // Active claims (not completed or rejected)
        List<Claim.ClaimStatus> activeStatuses = Arrays.asList(
            Claim.ClaimStatus.SUBMITTED,
            Claim.ClaimStatus.UNDER_REVIEW,
            Claim.ClaimStatus.APPROVED_BY_CLAIMS,
            Claim.ClaimStatus.FORWARDED_TO_FINANCE
        );
        long activeClaims = claimRepository.findAll().stream()
            .filter(claim -> activeStatuses.contains(claim.getStatus()))
            .count();
        model.addAttribute("activeClaims", activeClaims);
        
        // Pending approvals (claims needing review)
        long pendingApprovals = claimRepository.findAll().stream()
            .filter(claim -> claim.getStatus() == Claim.ClaimStatus.SUBMITTED || 
                           claim.getStatus() == Claim.ClaimStatus.UNDER_REVIEW)
            .count();
        model.addAttribute("pendingApprovals", pendingApprovals);
        
        // Calculate monthly revenue from active policies
        YearMonth currentMonth = YearMonth.now();
        BigDecimal monthlyRevenue = policyHolderRepository.findByStatus(PolicyHolder.PolicyStatus.ACTIVE).stream()
            .map(ph -> ph.getPolicy().getPremiumAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.US);
        model.addAttribute("monthlyRevenue", currencyFormat.format(monthlyRevenue));
        
        return "admin/dashboard-simple";
    }
    
    /**
     * Policy Holder Dashboard with real-time data
     */
    @GetMapping("/policyholder/dashboard")
    public String policyHolderDashboard(Model model, HttpServletRequest request, Authentication authentication) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "My Dashboard");
        
        // Default values for users without policy - MUST set all attributes to prevent Thymeleaf errors
        model.addAttribute("hasPolicyHolder", false);
        model.addAttribute("totalClaims", 0);
        model.addAttribute("activeClaims", 0L);
        model.addAttribute("totalAppointments", 0);
        model.addAttribute("upcomingAppointments", 0L);
        model.addAttribute("coverageUsedPercentage", 0.0);
        model.addAttribute("hasNextAppointment", false);
        model.addAttribute("recentClaims", new java.util.ArrayList<>());
        model.addAttribute("canBookAppointments", false);
        model.addAttribute("policyType", null);
        model.addAttribute("policyStatus", null);
        model.addAttribute("myPolicy", null);
        model.addAttribute("policyHolder", null);
        
        try {
            // Get current user
            String email = authentication.getName();
            Optional<User> userOpt = userRepository.findByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Optional<PolicyHolder> policyHolderOpt = policyHolderRepository.findByUser(user);
                
                if (policyHolderOpt.isPresent()) {
                    PolicyHolder policyHolder = policyHolderOpt.get();
                    model.addAttribute("hasPolicyHolder", true);
                    model.addAttribute("policyHolder", policyHolder);
                    
                    // My active policy
                    Policy policy = policyHolder.getPolicy();
                    model.addAttribute("myPolicy", policy);
                    model.addAttribute("policyStatus", policyHolder.getStatus());
                    
                    // Check if user can book appointments (PREMIUM or SENIOR only)
                    Policy.PolicyType policyType = policy.getType();
                    boolean canBookAppointments = (policyType == Policy.PolicyType.PREMIUM || policyType == Policy.PolicyType.SENIOR);
                    model.addAttribute("canBookAppointments", canBookAppointments);
                    model.addAttribute("policyType", policyType);
                    
                    // Policy holder personal info
                    model.addAttribute("policyHolderName", user.getFullName());
                    
                    // My claims
                    List<Claim> myClaims = claimRepository.findByPolicyHolder(policyHolder);
                    model.addAttribute("totalClaims", myClaims.size());
                    
                    long activeClaimsCount = myClaims.stream()
                        .filter(c -> c.getStatus() != Claim.ClaimStatus.APPROVED_BY_FINANCE && 
                                   c.getStatus() != Claim.ClaimStatus.REJECTED)
                        .count();
                    model.addAttribute("activeClaims", activeClaimsCount);
                    
                    // Recent claims (last 5)
                    List<Claim> recentClaims = myClaims.stream()
                        .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                        .limit(5)
                        .toList();
                    model.addAttribute("recentClaims", recentClaims);
                    
                    // Calculate coverage used percentage
                    BigDecimal totalClaimedAmount = myClaims.stream()
                        .filter(c -> c.getStatus() == Claim.ClaimStatus.APPROVED_BY_FINANCE)
                        .map(Claim::getAmountClaimed)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    double coverageUsedPercentage = 0.0;
                    if (policy.getCoverageAmount().compareTo(BigDecimal.ZERO) > 0) {
                        coverageUsedPercentage = totalClaimedAmount
                            .divide(policy.getCoverageAmount(), 4, java.math.RoundingMode.HALF_UP)
                            .multiply(new BigDecimal(100))
                            .doubleValue();
                    }
                    model.addAttribute("coverageUsedPercentage", Math.min(coverageUsedPercentage, 100.0));
                    
                    // My appointments
                    List<Appointment> myAppointments = appointmentRepository.findByPolicyHolder(policyHolder);
                    model.addAttribute("totalAppointments", myAppointments.size());
                    
                    long upcomingAppointmentsCount = myAppointments.stream()
                        .filter(a -> (a.getAppointmentDate().isAfter(LocalDate.now()) || 
                                    a.getAppointmentDate().equals(LocalDate.now())) &&
                                   (a.getStatus() == Appointment.AppointmentStatus.SCHEDULED || 
                                    a.getStatus() == Appointment.AppointmentStatus.CONFIRMED))
                        .count();
                    model.addAttribute("upcomingAppointments", upcomingAppointmentsCount);
                    
                    // Next appointment (nearest future appointment)
                    Optional<Appointment> nextAppointmentOpt = myAppointments.stream()
                        .filter(a -> (a.getAppointmentDate().isAfter(LocalDate.now()) || 
                                    (a.getAppointmentDate().equals(LocalDate.now()) && 
                                     a.getAppointmentTime().isAfter(java.time.LocalTime.now()))) &&
                                   (a.getStatus() == Appointment.AppointmentStatus.SCHEDULED || 
                                    a.getStatus() == Appointment.AppointmentStatus.CONFIRMED))
                        .min((a1, a2) -> {
                            int dateCompare = a1.getAppointmentDate().compareTo(a2.getAppointmentDate());
                            if (dateCompare != 0) return dateCompare;
                            return a1.getAppointmentTime().compareTo(a2.getAppointmentTime());
                        });
                    
                    if (nextAppointmentOpt.isPresent()) {
                        model.addAttribute("hasNextAppointment", true);
                        model.addAttribute("nextAppointment", nextAppointmentOpt.get());
                    } else {
                        model.addAttribute("hasNextAppointment", false);
                    }
                }
            }
        } catch (Exception e) {
            // Log error but don't break the page - defaults are already set
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "policyholder/dashboard";
    }
    
    /**
     * Doctor Dashboard with real-time data
     */
    @GetMapping("/doctor/dashboard")
    public String doctorDashboard(Model model, HttpServletRequest request, Authentication authentication) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "Doctor Dashboard");
        
        // Get current doctor
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Optional<Doctor> doctorOpt = doctorRepository.findByUser(user);
            
            if (doctorOpt.isPresent()) {
                Doctor doctor = doctorOpt.get();
                
                // Today's appointments
                List<Appointment> todayAppointments = appointmentRepository
                    .findByDoctorAndAppointmentDate(doctor, LocalDate.now());
                model.addAttribute("todayAppointments", todayAppointments.size());
                
                // All appointments
                List<Appointment> allAppointments = appointmentRepository.findByDoctor(doctor);
                model.addAttribute("totalAppointments", allAppointments.size());
                
                // Scheduled appointments
                long scheduledAppointments = allAppointments.stream()
                    .filter(a -> a.getStatus() == Appointment.AppointmentStatus.SCHEDULED || 
                               a.getStatus() == Appointment.AppointmentStatus.CONFIRMED)
                    .filter(a -> a.getAppointmentDate().isAfter(LocalDate.now()) || 
                               a.getAppointmentDate().equals(LocalDate.now()))
                    .count();
                model.addAttribute("scheduledAppointments", scheduledAppointments);
                
                // Completed appointments
                long completedAppointments = allAppointments.stream()
                    .filter(a -> a.getStatus() == Appointment.AppointmentStatus.COMPLETED)
                    .count();
                model.addAttribute("completedAppointments", completedAppointments);
                
                // Unique patients
                long uniquePatients = allAppointments.stream()
                    .map(a -> a.getPolicyHolder().getId())
                    .distinct()
                    .count();
                model.addAttribute("totalPatients", uniquePatients);
            }
        }
        
        return "doctor/dashboard";
    }
    
    /**
     * Agent Dashboard with real-time data
     */
    @GetMapping("/agent/dashboard")
    public String agentDashboard(Model model, HttpServletRequest request, Authentication authentication) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "Agent Dashboard");
        
        // Total active policy holders (as clients)
        long totalClients = policyHolderRepository.findByStatus(PolicyHolder.PolicyStatus.ACTIVE).size();
        model.addAttribute("totalClients", totalClients);
        
        // Total active policies
        long activePolicies = policyHolderRepository.findByStatus(PolicyHolder.PolicyStatus.ACTIVE).size();
        model.addAttribute("activePolicies", activePolicies);
        
        // Calculate potential commission (5% of total premiums)
        BigDecimal totalPremiums = policyHolderRepository.findByStatus(PolicyHolder.PolicyStatus.ACTIVE).stream()
            .map(ph -> ph.getPolicy().getPremiumAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal commission = totalPremiums.multiply(new BigDecimal("0.05"));
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.US);
        model.addAttribute("totalCommission", currencyFormat.format(commission));
        
        // Recent policies (this month)
        long recentPolicies = policyHolderRepository.findByStatus(PolicyHolder.PolicyStatus.ACTIVE).stream()
            .filter(ph -> ph.getCreatedAt().getMonth() == LocalDate.now().getMonth() &&
                         ph.getCreatedAt().getYear() == LocalDate.now().getYear())
            .count();
        model.addAttribute("recentPolicies", recentPolicies);
        
        return "agent/dashboard";
    }
}

