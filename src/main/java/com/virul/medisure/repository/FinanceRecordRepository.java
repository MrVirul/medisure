package com.virul.medisure.repository;

import com.virul.medisure.model.FinanceRecord;
import com.virul.medisure.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanceRecordRepository extends JpaRepository<FinanceRecord, Long> {
    List<FinanceRecord> findByClaim(Claim claim);
    List<FinanceRecord> findByStatus(FinanceRecord.FinanceStatus status);
}
