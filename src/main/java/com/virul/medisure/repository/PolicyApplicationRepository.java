package com.virul.medisure.repository;

import com.virul.medisure.sales.PolicyApplication;
import com.virul.medisure.sales.PolicyApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyApplicationRepository extends JpaRepository<PolicyApplication, Long> {
    
    Page<PolicyApplication> findByStatus(PolicyApplicationStatus status, Pageable pageable);
    
    Page<PolicyApplication> findByPolicyHolderId(Long policyHolderId, Pageable pageable);
    
    Page<PolicyApplication> findByPolicyId(Long policyId, Pageable pageable);
    
    @Query("SELECT pa FROM PolicyApplication pa WHERE pa.status IN :statuses")
    Page<PolicyApplication> findByStatusIn(@Param("statuses") List<PolicyApplicationStatus> statuses, Pageable pageable);
    
    List<PolicyApplication> findByPolicyHolderIdAndStatus(Long policyHolderId, PolicyApplicationStatus status);
}