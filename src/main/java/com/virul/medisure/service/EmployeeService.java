package com.virul.medisure.service;

import com.virul.medisure.dto.EmployeeCreateRequest;
import com.virul.medisure.dto.EmployeeResponse;
import com.virul.medisure.dto.EmployeeUpdateRequest;
import com.virul.medisure.user.Employee;
import com.virul.medisure.user.UserRole;
import com.virul.medisure.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        // Check if email already exists
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Check if phone number already exists (if provided)
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty() &&
            employeeRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }
        
        // Check if employee ID already exists
        if (employeeRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists");
        }
        
        Employee employee = new Employee();
        employee.setUsername(request.getUsername());
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setEmployeeId(request.getEmployeeId());
        employee.setDepartment(request.getDepartment());
        employee.setHireDate(request.getHireDate());
        employee.setRole(request.getRole());
        employee.setActive(true);
        
        Employee savedEmployee = employeeRepository.save(employee);
        return mapToResponse(savedEmployee);
    }
    
    public Optional<EmployeeResponse> updateEmployee(Long id, EmployeeUpdateRequest request) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    // Check if email already exists (and it's not the current employee's email)
                    if (request.getEmail() != null && !request.getEmail().equals(employee.getEmail()) &&
                        employeeRepository.existsByEmail(request.getEmail())) {
                        throw new RuntimeException("Email already exists");
                    }
                    
                    // Check if phone number already exists (and it's not the current employee's phone number)
                    if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(employee.getPhoneNumber()) &&
                        !request.getPhoneNumber().isEmpty() &&
                        employeeRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                        throw new RuntimeException("Phone number already exists");
                    }
                    
                    // Update fields if provided
                    if (request.getUsername() != null) {
                        employee.setUsername(request.getUsername());
                    }
                    if (request.getEmail() != null) {
                        employee.setEmail(request.getEmail());
                    }
                    if (request.getFirstName() != null) {
                        employee.setFirstName(request.getFirstName());
                    }
                    if (request.getLastName() != null) {
                        employee.setLastName(request.getLastName());
                    }
                    if (request.getPhoneNumber() != null) {
                        employee.setPhoneNumber(request.getPhoneNumber());
                    }
                    if (request.getEmployeeId() != null) {
                        employee.setEmployeeId(request.getEmployeeId());
                    }
                    if (request.getDepartment() != null) {
                        employee.setDepartment(request.getDepartment());
                    }
                    if (request.getHireDate() != null) {
                        employee.setHireDate(request.getHireDate());
                    }
                    if (request.getRole() != null) {
                        employee.setRole(request.getRole());
                    }
                    if (request.getActive() != null) {
                        employee.setActive(request.getActive());
                    }
                    
                    Employee updatedEmployee = employeeRepository.save(employee);
                    return mapToResponse(updatedEmployee);
                });
    }
    
    public boolean deleteEmployee(Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll(pageable);
        return employees.map(this::mapToResponse);
    }
    
    public Page<EmployeeResponse> getEmployeesByRole(UserRole role, Pageable pageable) {
        Page<Employee> employees = employeeRepository.findByRole(role, pageable);
        return employees.map(this::mapToResponse);
    }
    
    public boolean toggleEmployeeStatus(Long id) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setActive(!employee.getActive());
                    employeeRepository.save(employee);
                    return true;
                })
                .orElse(false);
    }
    
    public Page<EmployeeResponse> getActiveEmployees(Boolean active, Pageable pageable) {
        Page<Employee> employees = employeeRepository.findByActive(active, pageable);
        return employees.map(this::mapToResponse);
    }
    
    public Page<EmployeeResponse> getEmployeesByRoleAndStatus(UserRole role, Boolean active, Pageable pageable) {
        Page<Employee> employees = employeeRepository.findByRoleAndActive(role, active, pageable);
        return employees.map(this::mapToResponse);
    }
    
    private EmployeeResponse mapToResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setUsername(employee.getUsername());
        response.setEmail(employee.getEmail());
        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());
        response.setPhoneNumber(employee.getPhoneNumber());
        response.setEmployeeId(employee.getEmployeeId());
        response.setDepartment(employee.getDepartment());
        response.setHireDate(employee.getHireDate());
        response.setRole(employee.getRole());
        response.setActive(employee.getActive());
        response.setCreatedAt(employee.getCreatedAt());
        response.setUpdatedAt(employee.getUpdatedAt());
        return response;
    }
}