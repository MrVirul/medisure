package com.virul.medisure.service;

import com.virul.medisure.dto.AppointmentRequest;
import com.virul.medisure.model.*;
import com.virul.medisure.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PolicyHolderService policyHolderService;
    private final DoctorService doctorService;
    private final AuditLogService auditLogService;

    public Appointment bookAppointment(Long policyHolderId, AppointmentRequest request) {
        PolicyHolder policyHolder = policyHolderService.getPolicyHolderById(policyHolderId);
        Doctor doctor = doctorService.getDoctorById(request.getDoctorId());

        // No need to check time slots - premium and senior users can book any time

        Appointment appointment = new Appointment();
        appointment.setPolicyHolder(policyHolder);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        appointment.setReason(request.getReason());
        appointment.setNotes(request.getNotes());

        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Log the appointment booking with safe access to user data
        try {
            String userEmail = policyHolder.getUser() != null ? policyHolder.getUser().getEmail() : "Unknown";
            String doctorName = doctor.getUser() != null ? doctor.getUser().getFullName() : "Unknown Doctor";
            auditLogService.logAction(
                AuditLog.EntityType.APPOINTMENT,
                savedAppointment.getId(),
                AuditLog.Action.CREATE,
                userEmail,
                "Appointment booked with Dr. " + doctorName
            );
        } catch (Exception e) {
            // If audit logging fails, don't fail the appointment booking
            System.err.println("Failed to log appointment booking: " + e.getMessage());
        }

        return savedAppointment;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        Doctor doctor = doctorService.getDoctorById(doctorId);
        return appointmentRepository.findByDoctor(doctor);
    }

    public List<Appointment> getAppointmentsByPolicyHolder(Long policyHolderId) {
        PolicyHolder policyHolder = policyHolderService.getPolicyHolderById(policyHolderId);
        return appointmentRepository.findByPolicyHolder(policyHolder);
    }

    public List<Appointment> getAppointmentsByStatus(Appointment.AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public Appointment updateAppointmentStatus(Long id, Appointment.AppointmentStatus status) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(status);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Log the status update
        auditLogService.logAction(
            AuditLog.EntityType.APPOINTMENT,
            id,
            AuditLog.Action.UPDATE,
            "DOCTOR",
            "Appointment status updated to: " + status
        );

        return savedAppointment;
    }

    public List<Appointment> getTodayAppointments(Long doctorId) {
        Doctor doctor = doctorService.getDoctorById(doctorId);
        return appointmentRepository.findByDoctorAndAppointmentDate(doctor, LocalDate.now());
    }

    public Appointment updateAppointment(Long appointmentId, Long policyHolderId, AppointmentRequest request) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        // Verify the appointment belongs to the policy holder
        if (!appointment.getPolicyHolder().getId().equals(policyHolderId)) {
            throw new RuntimeException("You are not authorized to edit this appointment");
        }
        
        // Only allow editing of pending or scheduled appointments
        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING && 
            appointment.getStatus() != Appointment.AppointmentStatus.SCHEDULED) {
            throw new RuntimeException("Cannot edit appointment with status: " + appointment.getStatus());
        }
        
        Doctor doctor = doctorService.getDoctorById(request.getDoctorId());
        
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setReason(request.getReason());
        appointment.setNotes(request.getNotes());
        appointment.setStatus(Appointment.AppointmentStatus.PENDING); // Reset to pending on edit
        appointment.setUpdatedAt(java.time.LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Log the update with safe access
        try {
            String userEmail = appointment.getPolicyHolder() != null && appointment.getPolicyHolder().getUser() != null 
                ? appointment.getPolicyHolder().getUser().getEmail() : "Unknown";
            auditLogService.logAction(
                AuditLog.EntityType.APPOINTMENT,
                appointmentId,
                AuditLog.Action.UPDATE,
                userEmail,
                "Appointment updated"
            );
        } catch (Exception e) {
            System.err.println("Failed to log appointment update: " + e.getMessage());
        }
        
        return savedAppointment;
    }

    public void deleteAppointment(Long appointmentId, Long policyHolderId) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        // Verify the appointment belongs to the policy holder
        if (!appointment.getPolicyHolder().getId().equals(policyHolderId)) {
            throw new RuntimeException("You are not authorized to delete this appointment");
        }
        
        // Only allow deletion of pending or scheduled appointments
        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING && 
            appointment.getStatus() != Appointment.AppointmentStatus.SCHEDULED) {
            throw new RuntimeException("Cannot delete appointment with status: " + appointment.getStatus());
        }
        
        // Log before deleting with safe access
        try {
            String userEmail = appointment.getPolicyHolder() != null && appointment.getPolicyHolder().getUser() != null 
                ? appointment.getPolicyHolder().getUser().getEmail() : "Unknown";
            auditLogService.logAction(
                AuditLog.EntityType.APPOINTMENT,
                appointmentId,
                AuditLog.Action.DELETE,
                userEmail,
                "Appointment deleted"
            );
        } catch (Exception e) {
            System.err.println("Failed to log appointment deletion: " + e.getMessage());
        }
        
        appointmentRepository.delete(appointment);
    }

    public Appointment acceptAppointment(Long appointmentId, Long doctorId) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        // Verify the appointment belongs to the doctor
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("You are not authorized to accept this appointment");
        }
        
        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new RuntimeException("Only pending appointments can be accepted");
        }
        
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
        appointment.setUpdatedAt(java.time.LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Log the acceptance with safe access
        try {
            String doctorEmail = appointment.getDoctor() != null && appointment.getDoctor().getUser() != null 
                ? appointment.getDoctor().getUser().getEmail() : "Unknown";
            auditLogService.logAction(
                AuditLog.EntityType.APPOINTMENT,
                appointmentId,
                AuditLog.Action.UPDATE,
                doctorEmail,
                "Appointment accepted by doctor"
            );
        } catch (Exception e) {
            System.err.println("Failed to log appointment acceptance: " + e.getMessage());
        }
        
        return savedAppointment;
    }

    public Appointment rejectAppointment(Long appointmentId, Long doctorId, String rejectionReason) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        // Verify the appointment belongs to the doctor
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("You are not authorized to reject this appointment");
        }
        
        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING && 
            appointment.getStatus() != Appointment.AppointmentStatus.SCHEDULED) {
            throw new RuntimeException("Only pending or scheduled appointments can be rejected");
        }
        
        appointment.setStatus(Appointment.AppointmentStatus.REJECTED);
        appointment.setRejectionReason(rejectionReason);
        appointment.setUpdatedAt(java.time.LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Log the rejection with safe access
        try {
            String doctorEmail = appointment.getDoctor() != null && appointment.getDoctor().getUser() != null 
                ? appointment.getDoctor().getUser().getEmail() : "Unknown";
            auditLogService.logAction(
                AuditLog.EntityType.APPOINTMENT,
                appointmentId,
                AuditLog.Action.UPDATE,
                doctorEmail,
                "Appointment rejected: " + rejectionReason
            );
        } catch (Exception e) {
            System.err.println("Failed to log appointment rejection: " + e.getMessage());
        }
        
        return savedAppointment;
    }
}
