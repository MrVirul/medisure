package com.virul.medisure.repository;

import com.virul.medisure.user.PolicyHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyHolderRepository extends JpaRepository<PolicyHolder, Long> {
    
    Optional<PolicyHolder> findByPolicyNumber(String policyNumber);
    
    Optional<PolicyHolder> findByUsername(String username);
    
    Optional<PolicyHolder> findByEmail(String email);
    
    boolean existsByPolicyNumber(String policyNumber);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}