package com.virul.medisure.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "claim_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "file_url", nullable = false)
    private String fileUrl;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;
    
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();
    
    public enum DocumentType {
        MEDICAL_REPORT,
        PRESCRIPTION,
        BILL,
        RECEIPT,
        ID_PROOF,
        OTHER
    }
}
