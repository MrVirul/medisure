package com.virul.medisure.repository;

import com.virul.medisure.model.Claim;
import com.virul.medisure.model.PolicyHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByPolicyHolder(PolicyHolder policyHolder);
    List<Claim> findByStatus(Claim.ClaimStatus status);
}
