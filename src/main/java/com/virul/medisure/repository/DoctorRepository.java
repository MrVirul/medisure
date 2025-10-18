package com.virul.medisure.repository;

import com.virul.medisure.model.Doctor;
import com.virul.medisure.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByIsAvailableTrue();
    List<Doctor> findBySpecialization(String specialization);
    Optional<Doctor> findByRegistrationNo(String registrationNo);
    Optional<Doctor> findByUser(User user);
}
