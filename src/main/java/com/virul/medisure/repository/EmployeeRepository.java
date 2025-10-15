package com.virul.medisure.repository;

import com.virul.medisure.user.Employee;
import com.virul.medisure.user.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmployeeId(String employeeId);
    
    Optional<Employee> findByUsername(String username);
    
    Optional<Employee> findByEmail(String email);
    
    boolean existsByEmployeeId(String employeeId);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    Page<Employee> findByRole(UserRole role, Pageable pageable);
    
    @Query("SELECT e FROM Employee e WHERE e.active = :active")
    Page<Employee> findByActive(@Param("active") Boolean active, Pageable pageable);
    
    @Query("SELECT e FROM Employee e WHERE e.role = :role AND e.active = :active")
    Page<Employee> findByRoleAndActive(@Param("role") UserRole role, @Param("active") Boolean active, Pageable pageable);
}