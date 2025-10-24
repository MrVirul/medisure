package com.virul.medisure.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "policy_holders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyHolder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "authorities", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"})
    private User user;
    
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyStatus status;
    
    @Column(name = "policy_document_url")
    private String policyDocumentUrl;
    
    @Column(name = "policy_manager_approved")
    private Boolean policyManagerApproved = false;
    
    @Column(name = "policy_manager_approved_by")
    private String policyManagerApprovedBy;
    
    @Column(name = "policy_manager_approved_at")
    private LocalDateTime policyManagerApprovedAt;
    
    @Column(name = "finance_officer_approved")
    private Boolean financeOfficerApproved = false;
    
    @Column(name = "finance_officer_approved_by")
    private String financeOfficerApprovedBy;
    
    @Column(name = "finance_officer_approved_at")
    private LocalDateTime financeOfficerApprovedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum PolicyStatus {
        PENDING_APPROVAL,
        ACTIVE,
        EXPIRED,
        CANCELLED,
        SUSPENDED
    }
}
