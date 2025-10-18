package com.virul.medisure.controller;

import com.virul.medisure.dto.ApiResponse;
import com.virul.medisure.dto.DoctorRequest;
import com.virul.medisure.model.Appointment;
import com.virul.medisure.model.Doctor;
import com.virul.medisure.service.AppointmentService;
import com.virul.medisure.service.AuthService;
import com.virul.medisure.service.DoctorService;
import com.virul.medisure.model.User;
import com.virul.medisure.repository.DoctorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final AuthService authService;
    private final DoctorRepository doctorRepository;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Doctor>>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAvailableDoctors();
        return ResponseEntity.ok(ApiResponse.success(doctors));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Doctor>> getDoctorById(@PathVariable Long id) {
        try {
            Doctor doctor = doctorService.getDoctorById(id);
            return ResponseEntity.ok(ApiResponse.success(doctor));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICAL_COORDINATOR')")
    public ResponseEntity<ApiResponse<Doctor>> registerDoctor(@Valid @RequestBody DoctorRequest request) {
        try {
            Doctor doctor = doctorService.registerDoctor(request);
            return ResponseEntity.ok(ApiResponse.success("Doctor registered successfully", doctor));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-appointments")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<Appointment>>> getMyAppointments() {
        try {
            User user = authService.getCurrentUser();
            Doctor doctor = doctorRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
            List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctor.getId());
            return ResponseEntity.ok(ApiResponse.success(appointments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/today-appointments")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<Appointment>>> getTodayAppointments() {
        try {
            User user = authService.getCurrentUser();
            Doctor doctor = doctorRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
            List<Appointment> appointments = appointmentService.getTodayAppointments(doctor.getId());
            return ResponseEntity.ok(ApiResponse.success(appointments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/appointment/{id}/status")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Appointment>> updateAppointmentStatus(@PathVariable Long id, 
                                                                            @RequestBody Map<String, String> request) {
        try {
            String statusStr = request.get("status");
            Appointment.AppointmentStatus status = Appointment.AppointmentStatus.valueOf(statusStr);
            Appointment appointment = appointmentService.updateAppointmentStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Appointment status updated", appointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
