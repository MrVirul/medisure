package com.virul.medisure.sales;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "policy_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "policy_holder_id", nullable = false)
    private Long policyHolderId;
    
    @Column(name = "policy_id", nullable = false)
    private Long policyId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyApplicationStatus status;
    
    @Column(name = "submitted_documents")
    private String submittedDocuments;
    
    @Column(name = "review_notes")
    private String reviewNotes;
    
    @Column(name = "reviewed_by")
    private String reviewedBy;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}