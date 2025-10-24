package com.virul.medisure.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Claim {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_holder_id", nullable = false)
    @JsonIgnoreProperties({"user", "policy"})
    private PolicyHolder policyHolder;
    
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;
    
    @Column(name = "claim_date", nullable = false)
    private LocalDate claimDate;
    
    @Column(name = "amount_claimed", nullable = false)
    private BigDecimal amountClaimed;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;
    
    private String remarks;
    
    @Column(name = "medical_diagnosis")
    private String medicalDiagnosis;
    
    @Column(name = "hospital_name")
    private String hospitalName;
    
    @Column(name = "treatment_date")
    private LocalDate treatmentDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum ClaimStatus {
        SUBMITTED,
        UNDER_REVIEW,
        APPROVED_BY_CLAIMS,
        FORWARDED_TO_FINANCE,
        APPROVED_BY_FINANCE,
        REJECTED,
        REQUIRES_CORRECTION
    }
}
