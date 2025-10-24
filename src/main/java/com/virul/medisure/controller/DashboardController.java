package com.virul.medisure.controller;

import com.virul.medisure.dto.CreateEmployeeRequest;
import com.virul.medisure.dto.UpdateEmployeeRequest;
import com.virul.medisure.model.*;
import com.virul.medisure.repository.*;
import com.virul.medisure.service.AppointmentService;
import com.virul.medisure.service.AuthService;
import com.virul.medisure.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final UserService userService;
    private final AppointmentService appointmentService;
    private final AuthService authService;
    
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
     * Admin Dashboard - Simple Welcome Page
     */
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "Admin Dashboard");
        
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
        model.addAttribute("hasDoctorProfile", false);
        model.addAttribute("doctorName", null);
        model.addAttribute("doctorSpecialization", null);
        model.addAttribute("doctorAvailable", false);
        model.addAttribute("doctorUpdatedAt", null);
        model.addAttribute("todayAppointments", 0);
        model.addAttribute("completedToday", 0);
        model.addAttribute("upcomingToday", 0);
        model.addAttribute("totalAppointments", 0);
        model.addAttribute("scheduledAppointments", 0);
        model.addAttribute("completedAppointments", 0);
        model.addAttribute("totalPatients", 0);
        model.addAttribute("appointmentsThisMonth", 0);
        model.addAttribute("completedThisMonth", 0);
        model.addAttribute("todayAppointmentDetails", Collections.emptyList());
        model.addAttribute("upcomingAppointments", Collections.emptyList());
        model.addAttribute("pendingClaims", Collections.emptyList());
        model.addAttribute("pendingVerificationCount", 0);
        
        // Pending verification items shared across doctors
        List<Claim> pendingClaims = Stream.concat(
                claimRepository.findByStatus(Claim.ClaimStatus.SUBMITTED).stream(),
                claimRepository.findByStatus(Claim.ClaimStatus.UNDER_REVIEW).stream()
        )
        .sorted(Comparator.comparing(Claim::getCreatedAt).reversed())
        .limit(5)
        .collect(Collectors.toList());
        model.addAttribute("pendingClaims", pendingClaims);
        model.addAttribute("pendingVerificationCount", pendingClaims.size());
        
        // Get current doctor
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Optional<Doctor> doctorOpt = doctorRepository.findByUser(user);
            
            if (doctorOpt.isPresent()) {
                Doctor doctor = doctorOpt.get();
                model.addAttribute("hasDoctorProfile", true);
                model.addAttribute("doctorName", doctor.getUser().getFullName());
                model.addAttribute("doctorSpecialization", doctor.getSpecialization());
                model.addAttribute("doctorAvailable", Boolean.TRUE.equals(doctor.getIsAvailable()));
                model.addAttribute("doctorUpdatedAt", doctor.getUpdatedAt());
                
                // Today's appointments
                LocalDate today = LocalDate.now();
                LocalTime now = LocalTime.now();
                YearMonth currentMonth = YearMonth.from(today);
                
                List<Appointment> todayAppointments = appointmentRepository
                    .findByDoctorAndAppointmentDate(doctor, today)
                    .stream()
                    .sorted(Comparator.comparing(Appointment::getAppointmentTime))
                    .collect(Collectors.toList());
                model.addAttribute("todayAppointmentDetails", todayAppointments);
                model.addAttribute("todayAppointments", todayAppointments.size());
                
                long completedToday = todayAppointments.stream()
                    .filter(a -> a.getStatus() == Appointment.AppointmentStatus.COMPLETED)
                    .count();
                long upcomingToday = todayAppointments.stream()
                    .filter(a -> a.getStatus() == Appointment.AppointmentStatus.SCHEDULED ||
                                 a.getStatus() == Appointment.AppointmentStatus.CONFIRMED)
                    .filter(a -> !a.getAppointmentTime().isBefore(now))
                    .count();
                model.addAttribute("completedToday", completedToday);
                model.addAttribute("upcomingToday", upcomingToday);
                
                // All appointments
                List<Appointment> allAppointments = appointmentRepository.findByDoctor(doctor);
                model.addAttribute("totalAppointments", allAppointments.size());
                
                // Scheduled appointments
                long scheduledAppointments = allAppointments.stream()
                    .filter(a -> a.getStatus() == Appointment.AppointmentStatus.PENDING ||
                               a.getStatus() == Appointment.AppointmentStatus.SCHEDULED || 
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
                
                long appointmentsThisMonth = allAppointments.stream()
                    .filter(a -> YearMonth.from(a.getAppointmentDate()).equals(currentMonth))
                    .count();
                model.addAttribute("appointmentsThisMonth", appointmentsThisMonth);
                
                long completedThisMonth = allAppointments.stream()
                    .filter(a -> YearMonth.from(a.getAppointmentDate()).equals(currentMonth))
                    .filter(a -> a.getStatus() == Appointment.AppointmentStatus.COMPLETED)
                    .count();
                model.addAttribute("completedThisMonth", completedThisMonth);
                
                // Unique patients
                long uniquePatients = allAppointments.stream()
                    .map(a -> a.getPolicyHolder().getId())
                    .distinct()
                    .count();
                model.addAttribute("totalPatients", uniquePatients);
                
                List<Appointment> upcomingAppointments = allAppointments.stream()
                    .filter(a -> a.getStatus() == Appointment.AppointmentStatus.PENDING ||
                                 a.getStatus() == Appointment.AppointmentStatus.SCHEDULED ||
                                 a.getStatus() == Appointment.AppointmentStatus.CONFIRMED)
                    .filter(a -> {
                        LocalDate date = a.getAppointmentDate();
                        if (date.isAfter(today)) {
                            return true;
                        }
                        if (date.equals(today)) {
                            return !a.getAppointmentTime().isBefore(now);
                        }
                        return false;
                    })
                    .sorted(Comparator.comparing(Appointment::getAppointmentDate)
                        .thenComparing(Appointment::getAppointmentTime))
                    .limit(5)
                    .collect(Collectors.toList());
                model.addAttribute("upcomingAppointments", upcomingAppointments);
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
    
    /**
     * Pending Policy Approvals Page - DISABLED
     * Admin should only manage employees
     */
    // @GetMapping("/admin/pending-approvals")
    // public String pendingApprovalsPage(Model model, HttpServletRequest request) {
    //     model.addAttribute("currentPath", request.getRequestURI());
    //     model.addAttribute("pageTitle", "Pending Policy Approvals");
    //     return "admin/pending-approvals";
    // }
    
    /**
     * Admin Policies Page - DISABLED
     * This functionality has been removed for admin role
     */
    // @GetMapping("/admin/policies")
    // public String adminPoliciesPage(Model model, HttpServletRequest request) {
    //     model.addAttribute("currentPath", request.getRequestURI());
    //     model.addAttribute("pageTitle", "Manage Policies");
    //     return "admin/policies";
    // }
    
    /**
     * Admin Users Page - View All Employees
     * Admin can view all users and access utilities for managing employee accounts
     */
    @GetMapping("/admin/users")
    public String adminUsersPage(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "Employee Directory");
        
        // Get all users from database
        List<User> allUsers = userRepository.findAll();
        model.addAttribute("users", allUsers);
        
        // Calculate stats
        long totalUsers = allUsers.size();
        long staff = allUsers.stream()
            .filter(u -> u.getRole() != User.UserRole.POLICY_HOLDER && 
                        u.getRole() != User.UserRole.DOCTOR)
            .count();
        long doctors = allUsers.stream()
            .filter(u -> u.getRole() == User.UserRole.DOCTOR)
            .count();
        long agents = allUsers.stream()
            .filter(u -> u.getRole() == User.UserRole.SALES_OFFICER)
            .count();
        
        // Create stats object
        var stats = new java.util.HashMap<String, Long>();
        stats.put("totalUsers", totalUsers);
        stats.put("staff", staff);
        stats.put("doctors", doctors);
        stats.put("agents", agents);
        
        model.addAttribute("stats", stats);
        
        try {
            model.addAttribute("currentUserId", authService.getCurrentUser().getId());
        } catch (Exception ignored) {
            model.addAttribute("currentUserId", null);
        }
        
        return "admin/users";
    }

    @GetMapping("/admin/users/new")
    public String adminCreateUserPage(
            @ModelAttribute("employeeForm") CreateEmployeeRequest employeeForm,
            Model model,
            HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "Add Employee");
        model.addAttribute("availableRoles", getEmployeeRoles());
        model.addAttribute("globalErrors", Collections.emptyList());
        return "admin/create-user";
    }

    @PostMapping("/admin/users")
    public String adminCreateUser(
            @Valid @ModelAttribute("employeeForm") CreateEmployeeRequest employeeForm,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentPath", request.getRequestURI());
            model.addAttribute("pageTitle", "Add Employee");
            model.addAttribute("availableRoles", getEmployeeRoles());
            model.addAttribute("globalErrors", bindingResult.getGlobalErrors());
            return "admin/create-user";
        }

        try {
            if (!employeeForm.getPassword().equals(employeeForm.getConfirmPassword())) {
                bindingResult.rejectValue(
                    "confirmPassword",
                    "passwordMismatch",
                    "Passwords do not match"
                );
                model.addAttribute("currentPath", request.getRequestURI());
                model.addAttribute("pageTitle", "Add Employee");
                model.addAttribute("availableRoles", getEmployeeRoles());
                model.addAttribute("globalErrors", bindingResult.getGlobalErrors());
                return "admin/create-user";
            }

            if (!getEmployeeRoles().contains(employeeForm.getRole())) {
                bindingResult.rejectValue(
                    "role",
                    "invalidRole",
                    "Selected role is not eligible for employee creation"
                );
                model.addAttribute("currentPath", request.getRequestURI());
                model.addAttribute("pageTitle", "Add Employee");
                model.addAttribute("availableRoles", getEmployeeRoles());
                model.addAttribute("globalErrors", bindingResult.getGlobalErrors());
                return "admin/create-user";
            }

            userService.createEmployee(
                employeeForm.getFullName(),
                employeeForm.getEmail(),
                employeeForm.getPassword(),
                employeeForm.getPhone(),
                employeeForm.getRole()
            );
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Employee account created for " + employeeForm.getFullName()
            );
            return "redirect:/admin/users";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("employeeCreationError", e.getMessage());
        } catch (Exception e) {
            bindingResult.reject("employeeCreationError", "Unable to create employee. Please try again.");
        }

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "Add Employee");
        model.addAttribute("availableRoles", getEmployeeRoles());
        model.addAttribute("globalErrors", bindingResult.getGlobalErrors());
        return "admin/create-user";
    }

    @GetMapping("/admin/users/{id}/edit")
    public String adminEditUserPage(@PathVariable Long id,
                                    Model model,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "Edit Employee");

        try {
            User user = userService.getUserById(id);
            if (!getEmployeeRoles().contains(user.getRole())) {
                redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Only employee accounts can be edited from this page."
                );
                return "redirect:/admin/users";
            }

            UpdateEmployeeRequest form = new UpdateEmployeeRequest(
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
            );

            model.addAttribute("employeeForm", form);
            model.addAttribute("availableRoles", getEmployeeRoles());
            model.addAttribute("globalErrors", Collections.emptyList());
            model.addAttribute("userId", id);

            try {
                var currentUser = authService.getCurrentUser();
                model.addAttribute("isEditingSelf", currentUser != null && currentUser.getId().equals(id));
            } catch (Exception ignored) {
                model.addAttribute("isEditingSelf", false);
            }

            return "admin/edit-user";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to load employee account.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/edit")
    public String adminUpdateUser(@PathVariable Long id,
                                  @Valid @ModelAttribute("employeeForm") UpdateEmployeeRequest employeeForm,
                                  BindingResult bindingResult,
                                  Model model,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "Edit Employee");
        model.addAttribute("availableRoles", getEmployeeRoles());
        model.addAttribute("userId", id);

        User currentUser = null;
        try {
            currentUser = authService.getCurrentUser();
        } catch (Exception ignored) {
        }

        if (!getEmployeeRoles().contains(employeeForm.getRole())) {
            bindingResult.rejectValue("role", "invalidRole", "Selected role is not eligible for employee accounts.");
        }

        if (currentUser != null && currentUser.getId().equals(id) && employeeForm.getRole() != User.UserRole.ADMIN) {
            bindingResult.rejectValue("role", "cannotDowngradeSelf", "You cannot remove the ADMIN role from your own account.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("globalErrors", bindingResult.getGlobalErrors());
            model.addAttribute("isEditingSelf", currentUser != null && currentUser.getId().equals(id));
            return "admin/edit-user";
        }

        try {
            userService.updateUser(
                id,
                employeeForm.getFullName(),
                employeeForm.getEmail(),
                employeeForm.getPhone(),
                employeeForm.getRole()
            );
            redirectAttributes.addFlashAttribute("successMessage", "Employee account updated successfully.");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("updateError", ex.getMessage());
        } catch (Exception ex) {
            bindingResult.reject("updateError", "Unable to update employee. Please try again.");
        }

        model.addAttribute("globalErrors", bindingResult.getGlobalErrors());
        model.addAttribute("isEditingSelf", currentUser != null && currentUser.getId().equals(id));
        return "admin/edit-user";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String adminDeleteUser(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {
        User currentUser = null;
        try {
            currentUser = authService.getCurrentUser();
        } catch (Exception ignored) {
        }

        if (currentUser != null && currentUser.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot delete your own account.");
            return "redirect:/admin/users";
        }

        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee account deleted.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to delete employee at this time.");
        }

        return "redirect:/admin/users";
    }
    
    /**
     * Admin Claims Page - DISABLED
     * Admin should only manage employees
     */
    // @GetMapping("/admin/claims")
    // public String adminClaimsPage(Model model, HttpServletRequest request) {
    //     model.addAttribute("currentPath", request.getRequestURI());
    //     model.addAttribute("pageTitle", "Review Claims");
    //     return "admin/claims";
    // }
    
    private List<User.UserRole> getEmployeeRoles() {
        return Arrays.stream(User.UserRole.values())
            .filter(role -> role != User.UserRole.POLICY_HOLDER && role != User.UserRole.USER)
            .toList();
    }

    /**
     * Doctor Appointments Page
     */
    @GetMapping("/doctor/appointments")
    public String doctorAppointments(Model model,
                                     HttpServletRequest request,
                                     Authentication authentication) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "My Appointments");
        model.addAttribute("appointments", Collections.emptyList());
        model.addAttribute("hasDoctorProfile", false);
        model.addAttribute("upcomingAppointments", Collections.emptyList());
        model.addAttribute("pastAppointments", Collections.emptyList());

        if (authentication == null) {
            model.addAttribute("errorMessage", "Unable to load appointments. Please sign in again.");
            return "doctor/appointments";
        }

        Optional<User> userOpt = userRepository.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Doctor account not found.");
            return "doctor/appointments";
        }

        Optional<Doctor> doctorOpt = doctorRepository.findByUser(userOpt.get());
        if (doctorOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Doctor profile not linked to this account.");
            return "doctor/appointments";
        }

        Doctor doctor = doctorOpt.get();
        model.addAttribute("hasDoctorProfile", true);
        model.addAttribute("doctorName", doctor.getUser().getFullName());

        List<Appointment> appointments = appointmentRepository.findByDoctor(doctor).stream()
            .sorted(Comparator.comparing(Appointment::getAppointmentDate)
                .thenComparing(Appointment::getAppointmentTime))
            .toList();

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Appointment> upcoming = appointments.stream()
            .filter(appt -> {
                if (appt.getAppointmentDate().isAfter(today)) {
                    return true;
                }
                if (appt.getAppointmentDate().isEqual(today)) {
                    return !appt.getAppointmentTime().isBefore(now);
                }
                return false;
            })
            .toList();

        List<Appointment> past = appointments.stream()
            .filter(appt -> appt.getAppointmentDate().isBefore(today) ||
                (appt.getAppointmentDate().isEqual(today) && appt.getAppointmentTime().isBefore(now)))
            .sorted(Comparator.comparing(Appointment::getAppointmentDate).reversed()
                .thenComparing(Appointment::getAppointmentTime).reversed())
            .toList();

        model.addAttribute("appointments", appointments);
        model.addAttribute("upcomingAppointments", upcoming);
        model.addAttribute("pastAppointments", past);
        return "doctor/appointments";
    }

    @PostMapping("/doctor/appointments/{id}/status")
    public String doctorUpdateAppointmentStatus(@PathVariable Long id,
                                                @RequestParam("action") String action,
                                                @RequestParam(value = "rejectionReason", required = false) String rejectionReason,
                                                Authentication authentication,
                                                RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be signed in to update appointments.");
            return "redirect:/doctor/appointments";
        }

        Optional<User> userOpt = userRepository.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Doctor account not found.");
            return "redirect:/doctor/appointments";
        }

        Optional<Doctor> doctorOpt = doctorRepository.findByUser(userOpt.get());
        if (doctorOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Doctor profile not linked to this account.");
            return "redirect:/doctor/appointments";
        }

        Doctor doctor = doctorOpt.get();
        
        try {
            String successMessage;
            
            switch (action.toLowerCase()) {
                case "accept" -> {
                    appointmentService.acceptAppointment(id, doctor.getId());
                    successMessage = "Appointment accepted successfully.";
                }
                case "reject" -> {
                    String reason = (rejectionReason != null && !rejectionReason.trim().isEmpty()) 
                        ? rejectionReason.trim() 
                        : "Doctor is unavailable at this time";
                    appointmentService.rejectAppointment(id, doctor.getId(), reason);
                    successMessage = "Appointment rejected.";
                }
                case "complete" -> {
                    Appointment appointment = appointmentService.getAppointmentById(id);
                    if (!appointment.getDoctor().getId().equals(doctor.getId())) {
                        redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to update this appointment.");
                        return "redirect:/doctor/appointments";
                    }
                    appointmentService.updateAppointmentStatus(id, Appointment.AppointmentStatus.COMPLETED);
                    successMessage = "Appointment marked as completed.";
                }
                default -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Unsupported action: " + action);
                    return "redirect:/doctor/appointments";
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/doctor/appointments";
    }
}
