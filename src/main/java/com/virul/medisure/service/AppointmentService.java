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

        // Check if doctor is available
        if (!doctor.getIsAvailable()) {
            throw new RuntimeException("Doctor is not available");
        }

        // Check if appointment time is available
        List<Appointment> existingAppointments = appointmentRepository
                .findByDoctorAndAppointmentDate(doctor, request.getAppointmentDate());
        
        boolean timeSlotTaken = existingAppointments.stream()
                .anyMatch(appointment -> appointment.getAppointmentTime().equals(request.getAppointmentTime()));
        
        if (timeSlotTaken) {
            throw new RuntimeException("Time slot is already booked");
        }

        Appointment appointment = new Appointment();
        appointment.setPolicyHolder(policyHolder);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
        appointment.setReason(request.getReason());
        appointment.setNotes(request.getNotes());

        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Log the appointment booking
        auditLogService.logAction(
            AuditLog.EntityType.APPOINTMENT,
            savedAppointment.getId(),
            AuditLog.Action.CREATE,
            policyHolder.getUser().getEmail(),
            "Appointment booked with Dr. " + doctor.getUser().getFullName()
        );

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
}
