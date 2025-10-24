package com.virul.medisure.config;

import com.virul.medisure.model.Doctor;
import com.virul.medisure.model.Policy;
import com.virul.medisure.model.User;
import com.virul.medisure.repository.DoctorRepository;
import com.virul.medisure.repository.PolicyRepository;
import com.virul.medisure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create admin user
        if (!userRepository.existsByEmail("admin@medicare.com")) {
            User admin = new User();
            admin.setFullName("Admin User");
            admin.setEmail("admin@medicare.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhone("1234567890");
            admin.setRole(User.UserRole.ADMIN);
            userRepository.save(admin);
        }

        // Create sample users for new roles
        if (!userRepository.existsByEmail("operation@medicare.com")) {
            User operationManager = new User();
            operationManager.setFullName("Operation Manager");
            operationManager.setEmail("operation@medicare.com");
            operationManager.setPassword(passwordEncoder.encode("operation123"));
            operationManager.setPhone("1234567891");
            operationManager.setRole(User.UserRole.OPERATION_MANAGER);
            userRepository.save(operationManager);
        }

        if (!userRepository.existsByEmail("sales@medicare.com")) {
            User salesOfficer = new User();
            salesOfficer.setFullName("Sales Officer");
            salesOfficer.setEmail("sales@medicare.com");
            salesOfficer.setPassword(passwordEncoder.encode("sales123"));
            salesOfficer.setPhone("1234567892");
            salesOfficer.setRole(User.UserRole.SALES_OFFICER);
            userRepository.save(salesOfficer);
        }

        if (!userRepository.existsByEmail("support@medicare.com")) {
            User customerSupport = new User();
            customerSupport.setFullName("Customer Support Officer");
            customerSupport.setEmail("support@medicare.com");
            customerSupport.setPassword(passwordEncoder.encode("support123"));
            customerSupport.setPhone("1234567893");
            customerSupport.setRole(User.UserRole.CUSTOMER_SUPPORT_OFFICER);
            userRepository.save(customerSupport);
        }

        // Create sample doctors with their Doctor profiles
        if (!userRepository.existsByEmail("doctor1@medicare.com")) {
            User doctorUser1 = new User();
            doctorUser1.setFullName("Dr. Sarah Johnson");
            doctorUser1.setEmail("doctor1@medicare.com");
            doctorUser1.setPassword(passwordEncoder.encode("doctor123"));
            doctorUser1.setPhone("1234567894");
            doctorUser1.setRole(User.UserRole.DOCTOR);
            User savedDoctor1 = userRepository.save(doctorUser1);
            
            // Create Doctor profile
            Doctor doctor1 = new Doctor();
            doctor1.setUser(savedDoctor1);
            doctor1.setSpecialization("Cardiologist");
            doctor1.setRegistrationNo("MED-CARD-001");
            doctor1.setIsAvailable(true);
            doctorRepository.save(doctor1);
        }

        if (!userRepository.existsByEmail("doctor2@medicare.com")) {
            User doctorUser2 = new User();
            doctorUser2.setFullName("Dr. Michael Chen");
            doctorUser2.setEmail("doctor2@medicare.com");
            doctorUser2.setPassword(passwordEncoder.encode("doctor123"));
            doctorUser2.setPhone("1234567895");
            doctorUser2.setRole(User.UserRole.DOCTOR);
            User savedDoctor2 = userRepository.save(doctorUser2);
            
            // Create Doctor profile
            Doctor doctor2 = new Doctor();
            doctor2.setUser(savedDoctor2);
            doctor2.setSpecialization("Pediatrician");
            doctor2.setRegistrationNo("MED-PED-001");
            doctor2.setIsAvailable(true);
            doctorRepository.save(doctor2);
        }

        if (!userRepository.existsByEmail("doctor3@medicare.com")) {
            User doctorUser3 = new User();
            doctorUser3.setFullName("Dr. Emily Rodriguez");
            doctorUser3.setEmail("doctor3@medicare.com");
            doctorUser3.setPassword(passwordEncoder.encode("doctor123"));
            doctorUser3.setPhone("1234567896");
            doctorUser3.setRole(User.UserRole.DOCTOR);
            User savedDoctor3 = userRepository.save(doctorUser3);
            
            // Create Doctor profile
            Doctor doctor3 = new Doctor();
            doctor3.setUser(savedDoctor3);
            doctor3.setSpecialization("General Physician");
            doctor3.setRegistrationNo("MED-GP-001");
            doctor3.setIsAvailable(true);
            doctorRepository.save(doctor3);
        }

        if (!userRepository.existsByEmail("doctor4@medicare.com")) {
            User doctorUser4 = new User();
            doctorUser4.setFullName("Dr. James Wilson");
            doctorUser4.setEmail("doctor4@medicare.com");
            doctorUser4.setPassword(passwordEncoder.encode("doctor123"));
            doctorUser4.setPhone("1234567897");
            doctorUser4.setRole(User.UserRole.DOCTOR);
            User savedDoctor4 = userRepository.save(doctorUser4);
            
            // Create Doctor profile
            Doctor doctor4 = new Doctor();
            doctor4.setUser(savedDoctor4);
            doctor4.setSpecialization("Orthopedic Surgeon");
            doctor4.setRegistrationNo("MED-ORTH-001");
            doctor4.setIsAvailable(true);
            doctorRepository.save(doctor4);
        }

        // Create sample policies
        if (policyRepository.count() == 0) {
            // Basic Policy
            Policy basic = new Policy();
            basic.setName("Basic Health Insurance");
            basic.setType(Policy.PolicyType.BASIC);
            basic.setCoverageAmount(new BigDecimal("100000"));
            basic.setPremiumAmount(new BigDecimal("5000"));
            basic.setDurationMonths(12);
            basic.setDescription("Basic health coverage for routine medical expenses and emergency treatments. Premium: Rs. 5,000, Coverage: Rs. 1,00,000");
            basic.setIsActive(true);
            policyRepository.save(basic);

            // Premium Policy
            Policy premium = new Policy();
            premium.setName("Premium Health Insurance");
            premium.setType(Policy.PolicyType.PREMIUM);
            premium.setCoverageAmount(new BigDecimal("500000"));
            premium.setPremiumAmount(new BigDecimal("15000"));
            premium.setDurationMonths(12);
            premium.setDescription("Premium coverage including doctor consultations, specialist treatments, and hospitalization. Premium: Rs. 15,000, Coverage: Rs. 5,00,000");
            premium.setIsActive(true);
            policyRepository.save(premium);

            // Family Policy
            Policy family = new Policy();
            family.setName("Family Health Insurance");
            family.setType(Policy.PolicyType.FAMILY);
            family.setCoverageAmount(new BigDecimal("750000"));
            family.setPremiumAmount(new BigDecimal("25000"));
            family.setDurationMonths(12);
            family.setDescription("Comprehensive family health coverage for up to 6 family members. Premium: Rs. 25,000, Coverage: Rs. 7,50,000");
            family.setIsActive(true);
            policyRepository.save(family);

            // Senior Policy
            Policy senior = new Policy();
            senior.setName("Senior Citizen Health Insurance");
            senior.setType(Policy.PolicyType.SENIOR);
            senior.setCoverageAmount(new BigDecimal("300000"));
            senior.setPremiumAmount(new BigDecimal("20000"));
            senior.setDurationMonths(12);
            senior.setDescription("Specialized health insurance for senior citizens with pre-existing condition coverage. Premium: Rs. 20,000, Coverage: Rs. 3,00,000");
            senior.setIsActive(true);
            policyRepository.save(senior);
        }
    }
}
