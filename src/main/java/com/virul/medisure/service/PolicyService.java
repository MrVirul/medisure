package com.virul.medisure.service;

import com.virul.medisure.policy.Policy;
import com.virul.medisure.policy.PolicyType;
import com.virul.medisure.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PolicyService {
    
    private final PolicyRepository policyRepository;
    
    public Policy createPolicy(Policy policy) {
        return policyRepository.save(policy);
    }
    
    public Optional<Policy> getPolicyById(Long id) {
        return policyRepository.findById(id);
    }
    
    public Page<Policy> getAllPolicies(Pageable pageable) {
        return policyRepository.findAll(pageable);
    }
    
    public Page<Policy> getActivePolicies(Boolean isActive, Pageable pageable) {
        return policyRepository.findByIsActive(isActive, pageable);
    }
    
    public Page<Policy> getPoliciesByType(PolicyType policyType, Pageable pageable) {
        return policyRepository.findByPolicyType(policyType, pageable);
    }
    
    public Page<Policy> getPoliciesByTypeAndStatus(PolicyType policyType, Boolean isActive, Pageable pageable) {
        return policyRepository.findByPolicyTypeAndIsActive(policyType, isActive, pageable);
    }
    
    public Page<Policy> searchPolicies(String searchTerm, Pageable pageable) {
        return policyRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
    }
    
    public Optional<Policy> updatePolicy(Long id, Policy policyDetails) {
        return policyRepository.findById(id)
                .map(policy -> {
                    policy.setPolicyType(policyDetails.getPolicyType());
                    policy.setName(policyDetails.getName());
                    policy.setDescription(policyDetails.getDescription());
                    policy.setCoverageAmount(policyDetails.getCoverageAmount());
                    policy.setPremiumAmount(policyDetails.getPremiumAmount());
                    policy.setDuration(policyDetails.getDuration());
                    policy.setTerms(policyDetails.getTerms());
                    policy.setIsActive(policyDetails.getIsActive());
                    
                    return policyRepository.save(policy);
                });
    }
    
    public boolean deletePolicy(Long id) {
        if (policyRepository.existsById(id)) {
            policyRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<Policy> getActivePoliciesList() {
        return policyRepository.findByIsActiveTrue();
    }
}