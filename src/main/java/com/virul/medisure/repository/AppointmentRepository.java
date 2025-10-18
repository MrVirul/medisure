package com.virul.medisure.repository;

import com.virul.medisure.model.Appointment;
import com.virul.medisure.model.Doctor;
import com.virul.medisure.model.PolicyHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByPolicyHolder(PolicyHolder policyHolder);
    List<Appointment> findByDoctorAndAppointmentDate(Doctor doctor, LocalDate appointmentDate);
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);
}
