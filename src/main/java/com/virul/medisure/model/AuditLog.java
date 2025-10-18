package com.virul.medisure.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;
    
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;
    
    @Column(name = "performed_by", nullable = false)
    private String performedBy;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    public enum EntityType {
        USER,
        POLICY,
        CLAIM,
        APPOINTMENT,
        DOCTOR,
        POLICY_HOLDER,
        FINANCE_RECORD
    }
    
    public enum Action {
        CREATE,
        UPDATE,
        DELETE,
        APPROVE,
        REJECT,
        FORWARD,
        LOGIN,
        LOGOUT
    }
}
