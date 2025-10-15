package com.virul.medisure.repository;

import com.virul.medisure.policy.Policy;
import com.virul.medisure.policy.PolicyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    
    List<Policy> findByIsActiveTrue();
    
    Page<Policy> findByIsActive(Boolean isActive, Pageable pageable);
    
    Page<Policy> findByPolicyType(PolicyType policyType, Pageable pageable);
    
    Page<Policy> findByPolicyTypeAndIsActive(PolicyType policyType, Boolean isActive, Pageable pageable);
    
    @Query("SELECT p FROM Policy p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Policy> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);
}