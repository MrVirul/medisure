package com.virul.medisure.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "finance_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanceRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;
    
    @ManyToOne
    @JoinColumn(name = "finance_manager_id", nullable = false)
    private User financeManager;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FinanceStatus status;
    
    private String remarks;
    
    @Column(name = "approved_amount")
    private Double approvedAmount;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt = LocalDateTime.now();
    
    public enum FinanceStatus {
        APPROVED,
        REJECTED,
        PENDING_REVIEW
    }
}
