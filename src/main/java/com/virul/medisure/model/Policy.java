package com.virul.medisure.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Policy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyType type;
    
    @Column(name = "coverage_amount", nullable = false)
    private BigDecimal coverageAmount;
    
    @Column(name = "premium_amount", nullable = false)
    private BigDecimal premiumAmount;
    
    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    public enum PolicyType {
        BASIC,
        PREMIUM,
        FAMILY,
        SENIOR
    }
}
