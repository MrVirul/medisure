package com.virul.medisure.repository;

import com.virul.medisure.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityTypeAndEntityId(AuditLog.EntityType entityType, Long entityId);
    List<AuditLog> findByPerformedBy(String performedBy);
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
