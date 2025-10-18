package com.virul.medisure.controller;

import com.virul.medisure.dto.ApiResponse;
import com.virul.medisure.dto.AppointmentRequest;
import com.virul.medisure.model.Appointment;
import com.virul.medisure.model.PolicyHolder;
import com.virul.medisure.service.AppointmentService;
import com.virul.medisure.service.AuthService;
import com.virul.medisure.service.PolicyHolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PolicyHolderService policyHolderService;
    private final AuthService authService;

    @PostMapping
    @PreAuthorize("hasRole('POLICY_HOLDER')")
    public ResponseEntity<ApiResponse<Appointment>> bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderByUser(user);
            
            // Check if policy is premium type
            if (policyHolder.getPolicy().getType() != com.virul.medisure.model.Policy.PolicyType.PREMIUM) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Appointments are only available for Premium policy holders"));
            }
            
            Appointment appointment = appointmentService.bookAppointment(policyHolder.getId(), request);
            return ResponseEntity.ok(ApiResponse.success("Appointment booked successfully", appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-appointments")
    @PreAuthorize("hasRole('POLICY_HOLDER')")
    public ResponseEntity<ApiResponse<List<Appointment>>> getMyAppointments() {
        try {
            var user = authService.getCurrentUser();
            PolicyHolder policyHolder = policyHolderService.getPolicyHolderByUser(user);
            List<Appointment> appointments = appointmentService.getAppointmentsByPolicyHolder(policyHolder.getId());
            return ResponseEntity.ok(ApiResponse.success(appointments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Appointment>> getAppointmentById(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            return ResponseEntity.ok(ApiResponse.success(appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
