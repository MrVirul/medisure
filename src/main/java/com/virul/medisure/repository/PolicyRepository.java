package com.virul.medisure.repository;

import com.virul.medisure.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByIsActiveTrue();
    List<Policy> findByType(Policy.PolicyType type);
}
