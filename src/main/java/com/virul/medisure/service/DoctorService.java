package com.virul.medisure.service;

import com.virul.medisure.dto.DoctorRequest;
import com.virul.medisure.model.Doctor;
import com.virul.medisure.model.User;
import com.virul.medisure.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserService userService;

    public Doctor registerDoctor(DoctorRequest request) {
        User user = userService.getUserById(request.getUserId());
        
        // Change user role to DOCTOR
        userService.changeUserRole(request.getUserId(), User.UserRole.DOCTOR);

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setSpecialization(request.getSpecialization());
        doctor.setRegistrationNo(request.getRegistrationNo());
        doctor.setIsAvailable(true);

        return doctorRepository.save(doctor);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findByIsAvailableTrue();
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public Doctor getDoctorByRegistrationNo(String registrationNo) {
        return doctorRepository.findByRegistrationNo(registrationNo)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public Doctor updateDoctor(Long id, String specialization, Boolean isAvailable) {
        Doctor doctor = getDoctorById(id);
        doctor.setSpecialization(specialization);
        doctor.setIsAvailable(isAvailable);
        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        Doctor doctor = getDoctorById(id);
        doctor.setIsAvailable(false);
        doctorRepository.save(doctor);
    }
}
