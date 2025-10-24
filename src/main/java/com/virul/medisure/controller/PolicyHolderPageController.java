package com.virul.medisure.controller;

import com.virul.medisure.dto.AppointmentRequest;
import com.virul.medisure.model.Appointment;
import com.virul.medisure.model.Doctor;
import com.virul.medisure.model.Policy;
import com.virul.medisure.model.PolicyHolder;
import com.virul.medisure.repository.PolicyHolderRepository;
import com.virul.medisure.service.AuthService;
import com.virul.medisure.service.DoctorService;
import com.virul.medisure.service.AppointmentService;
import com.virul.medisure.service.PolicyHolderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Controller for policyholder-specific pages (Thymeleaf views)
 */
@Controller
@RequestMapping("/policyholder")
@RequiredArgsConstructor
public class PolicyHolderPageController {
    
    private final PolicyHolderService policyHolderService;
    private final PolicyHolderRepository policyHolderRepository;
    private final AuthService authService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    
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
        model.addAttribute("canBookAppointments", false);
        model.addAttribute("policyType", "UNKNOWN");
        model.addAttribute("policyAppointments", Collections.emptyList());
        
        // Check if user can book appointments and collect their appointments
        try {
            var user = authService.getCurrentUser();
            Optional<PolicyHolder> policyHolderOpt = policyHolderRepository.findByUser(user);
            
            if (policyHolderOpt.isPresent()) {
                PolicyHolder policyHolder = policyHolderOpt.get();
                var policyType = policyHolder.getPolicy() != null
                        ? policyHolder.getPolicy().getType()
                        : null;
                
                boolean canBookAppointments = policyType == Policy.PolicyType.PREMIUM ||
                                              policyType == Policy.PolicyType.SENIOR;
                model.addAttribute("canBookAppointments", canBookAppointments);
                model.addAttribute("policyType", policyType != null ? policyType.name() : "NONE");
                
                List<Appointment> myAppointments = appointmentService
                        .getAppointmentsByPolicyHolder(policyHolder.getId()).stream()
                        .sorted(Comparator.comparing(Appointment::getAppointmentDate)
                            .thenComparing(Appointment::getAppointmentTime))
                        .toList();
                model.addAttribute("policyAppointments", myAppointments);
            } else {
                model.addAttribute("error", "We couldn't find an active policy linked to your account. Please contact support.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "We couldn't load your appointments right now. Please try again later.");
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
            prepareBookingForm(model, new AppointmentRequest());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Unable to verify your policy. Please contact support.");
            return "redirect:/policyholder/dashboard";
        }
        
        return "policyholder/book-appointment";
    }

    @PostMapping("/appointments/book")
    public String submitBookAppointment(@Valid @ModelAttribute("appointmentRequest") AppointmentRequest appointmentRequest,
                                        BindingResult bindingResult,
                                        Model model,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "Book Appointment");
        
        Policy.PolicyType policyType = null;
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderByUser(user);
            policyType = policyHolder.getPolicy().getType();

            if (policyType != Policy.PolicyType.PREMIUM && policyType != Policy.PolicyType.SENIOR) {
                redirectAttributes.addFlashAttribute("error", "Appointment booking is only available for PREMIUM and SENIOR policy holders. Please upgrade your policy to access this feature.");
                redirectAttributes.addFlashAttribute("policyType", policyType.toString());
                return "redirect:/policyholder/appointments";
            }
            
            // Check if policy is ACTIVE
            if (policyHolder.getStatus() != PolicyHolder.PolicyStatus.ACTIVE) {
                redirectAttributes.addFlashAttribute("error", "Your policy is not active yet (Status: " + policyHolder.getStatus() + "). Please wait for approval or contact support.");
                return "redirect:/policyholder/appointments";
            }

            // Ensure doctor selection is valid
            if (appointmentRequest.getDoctorId() != null) {
                try {
                    doctorService.getDoctorById(appointmentRequest.getDoctorId());
                } catch (RuntimeException ex) {
                    bindingResult.rejectValue("doctorId", "doctorNotFound", "Selected doctor could not be found.");
                }
            }

            // Additional time validation for same-day bookings
            if (appointmentRequest.getAppointmentDate() != null && appointmentRequest.getAppointmentTime() != null) {
                LocalDate today = LocalDate.now();
                if (appointmentRequest.getAppointmentDate().isEqual(today)) {
                    LocalTime now = LocalTime.now();
                    if (appointmentRequest.getAppointmentTime().isBefore(now)) {
                        bindingResult.rejectValue("appointmentTime", "timePast", "Please choose a time later than now.");
                    }
                }
            }

            if (bindingResult.hasErrors()) {
                model.addAttribute("canBookAppointments", true);
                model.addAttribute("policyType", policyType);
                model.addAttribute("globalErrors", bindingResult.getGlobalErrors());
                prepareBookingForm(model, appointmentRequest);
                return "policyholder/book-appointment";
            }

            appointmentService.bookAppointment(policyHolder.getId(), appointmentRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment booked successfully. We have sent the details to your dashboard.");
            return "redirect:/policyholder/appointments";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("bookingError", ex.getMessage());
        } catch (Exception e) {
            // Log the actual error for debugging
            System.err.println("Appointment booking error: " + e.getMessage());
            e.printStackTrace();
            bindingResult.reject("bookingError", "We could not book your appointment at this time. Error: " + e.getMessage());
        }

        model.addAttribute("canBookAppointments", true);
        if (policyType != null) {
            model.addAttribute("policyType", policyType);
        }
        model.addAttribute("globalErrors", bindingResult.getGlobalErrors());
        prepareBookingForm(model, appointmentRequest);
        return "policyholder/book-appointment";
    }

    private void prepareBookingForm(Model model, AppointmentRequest appointmentRequest) {
        List<Doctor> doctors = doctorService.getAvailableDoctors();
        model.addAttribute("availableDoctors", doctors);
        model.addAttribute("appointmentRequest", appointmentRequest);
        model.addAttribute("minDate", LocalDate.now());
        model.addAttribute("defaultTime", LocalTime.of(9, 0));
        if (!model.containsAttribute("globalErrors")) {
            model.addAttribute("globalErrors", List.of());
        }
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

    @GetMapping("/appointments/{id}/edit")
    public String editAppointmentPage(@PathVariable Long id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "Edit Appointment");
        
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderByUser(user);
            var policyType = policyHolder.getPolicy().getType();
            
            if (policyType != Policy.PolicyType.PREMIUM && policyType != Policy.PolicyType.SENIOR) {
                redirectAttributes.addFlashAttribute("error", "Appointment management is only available for PREMIUM and SENIOR policy holders.");
                return "redirect:/policyholder/appointments";
            }
            
            Appointment appointment = appointmentService.getAppointmentById(id);
            
            // Verify ownership
            if (!appointment.getPolicyHolder().getId().equals(policyHolder.getId())) {
                redirectAttributes.addFlashAttribute("error", "You are not authorized to edit this appointment.");
                return "redirect:/policyholder/appointments";
            }
            
            // Check if editable
            if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING && 
                appointment.getStatus() != Appointment.AppointmentStatus.SCHEDULED) {
                redirectAttributes.addFlashAttribute("error", "Cannot edit appointment with status: " + appointment.getStatus());
                return "redirect:/policyholder/appointments";
            }
            
            AppointmentRequest appointmentRequest = new AppointmentRequest();
            appointmentRequest.setDoctorId(appointment.getDoctor().getId());
            appointmentRequest.setAppointmentDate(appointment.getAppointmentDate());
            appointmentRequest.setAppointmentTime(appointment.getAppointmentTime());
            appointmentRequest.setReason(appointment.getReason());
            appointmentRequest.setNotes(appointment.getNotes());
            
            model.addAttribute("appointment", appointment);
            model.addAttribute("canBookAppointments", true);
            model.addAttribute("policyType", policyType);
            prepareBookingForm(model, appointmentRequest);
            
            return "policyholder/edit-appointment";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Unable to load appointment: " + e.getMessage());
            return "redirect:/policyholder/appointments";
        }
    }

    @PostMapping("/appointments/{id}/edit")
    public String submitEditAppointment(@PathVariable Long id,
                                        @Valid @ModelAttribute("appointmentRequest") AppointmentRequest appointmentRequest,
                                        BindingResult bindingResult,
                                        Model model,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pageTitle", "Edit Appointment");
        
        Policy.PolicyType policyType = null;
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderByUser(user);
            policyType = policyHolder.getPolicy().getType();

            if (policyType != Policy.PolicyType.PREMIUM && policyType != Policy.PolicyType.SENIOR) {
                redirectAttributes.addFlashAttribute("error", "Appointment management is only available for PREMIUM and SENIOR policy holders.");
                return "redirect:/policyholder/appointments";
            }

            // Ensure doctor selection is valid
            if (appointmentRequest.getDoctorId() != null) {
                try {
                    doctorService.getDoctorById(appointmentRequest.getDoctorId());
                } catch (RuntimeException ex) {
                    bindingResult.rejectValue("doctorId", "doctorNotFound", "Selected doctor could not be found.");
                }
            }

            // Additional time validation for same-day bookings
            if (appointmentRequest.getAppointmentDate() != null && appointmentRequest.getAppointmentTime() != null) {
                LocalDate today = LocalDate.now();
                if (appointmentRequest.getAppointmentDate().isEqual(today)) {
                    LocalTime now = LocalTime.now();
                    if (appointmentRequest.getAppointmentTime().isBefore(now)) {
                        bindingResult.rejectValue("appointmentTime", "timePast", "Please choose a time later than now.");
                    }
                }
            }

            if (bindingResult.hasErrors()) {
                Appointment appointment = appointmentService.getAppointmentById(id);
                model.addAttribute("appointment", appointment);
                model.addAttribute("canBookAppointments", true);
                model.addAttribute("policyType", policyType);
                model.addAttribute("globalErrors", bindingResult.getGlobalErrors());
                prepareBookingForm(model, appointmentRequest);
                return "policyholder/edit-appointment";
            }

            appointmentService.updateAppointment(id, policyHolder.getId(), appointmentRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment updated successfully.");
            return "redirect:/policyholder/appointments";
        } catch (Exception e) {
            bindingResult.reject("updateError", e.getMessage());
        }

        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            model.addAttribute("appointment", appointment);
        } catch (Exception ignored) {
        }
        model.addAttribute("canBookAppointments", true);
        if (policyType != null) {
            model.addAttribute("policyType", policyType);
        }
        model.addAttribute("globalErrors", bindingResult.getGlobalErrors());
        prepareBookingForm(model, appointmentRequest);
        return "policyholder/edit-appointment";
    }

    @PostMapping("/appointments/{id}/delete")
    public String deleteAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderByUser(user);
            var policyType = policyHolder.getPolicy().getType();
            
            if (policyType != Policy.PolicyType.PREMIUM && policyType != Policy.PolicyType.SENIOR) {
                redirectAttributes.addFlashAttribute("error", "Appointment management is only available for PREMIUM and SENIOR policy holders.");
                return "redirect:/policyholder/appointments";
            }
            
            appointmentService.deleteAppointment(id, policyHolder.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Appointment deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Unable to delete appointment: " + e.getMessage());
        }
        
        return "redirect:/policyholder/appointments";
    }
}
