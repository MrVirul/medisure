package com.virul.medisure.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Employee extends User {
    
    @Column(name = "employee_id", unique = true)
    private String employeeId;
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "hire_date")
    private LocalDate hireDate;
}