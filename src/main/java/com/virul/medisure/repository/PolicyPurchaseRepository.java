package com.virul.medisure.repository;

import com.virul.medisure.policy.PolicyPurchase;
import com.virul.medisure.policy.PolicyPurchaseStatus;
import com.virul.medisure.user.PolicyHolder;
import com.virul.medisure.policy.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PolicyPurchaseRepository extends JpaRepository<PolicyPurchase, Long> {
    
    List<PolicyPurchase> findByPolicyHolder(PolicyHolder policyHolder);
    
    List<PolicyPurchase> findByPolicy(Policy policy);
    
    Page<PolicyPurchase> findByStatus(PolicyPurchaseStatus status, Pageable pageable);
    
    Page<PolicyPurchase> findByPolicyHolder(PolicyHolder policyHolder, Pageable pageable);
    
    Page<PolicyPurchase> findByPolicy(Policy policy, Pageable pageable);
    
    @Query("SELECT pp FROM PolicyPurchase pp WHERE pp.expiryDate <= :date AND pp.status = 'ACTIVE'")
    List<PolicyPurchase> findExpiringPolicies(@Param("date") LocalDate date);
    
    @Query("SELECT pp FROM PolicyPurchase pp WHERE pp.policyHolder.id = :policyHolderId")
    Page<PolicyPurchase> findByPolicyHolderId(@Param("policyHolderId") Long policyHolderId, Pageable pageable);
    
    @Query("SELECT pp FROM PolicyPurchase pp WHERE pp.policy.id = :policyId")
    Page<PolicyPurchase> findByPolicyId(@Param("policyId") Long policyId, Pageable pageable);
}