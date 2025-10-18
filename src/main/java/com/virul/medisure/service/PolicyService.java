package com.virul.medisure.service;

import com.virul.medisure.dto.PolicyRequest;
import com.virul.medisure.model.Policy;
import com.virul.medisure.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    public List<Policy> getActivePolicies() {
        return policyRepository.findByIsActiveTrue();
    }

    public List<Policy> getPoliciesByType(Policy.PolicyType type) {
        return policyRepository.findByType(type);
    }

    public Policy getPolicyById(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
    }

    public Policy createPolicy(PolicyRequest request) {
        Policy policy = new Policy();
        policy.setName(request.getName());
        policy.setType(request.getType());
        policy.setCoverageAmount(request.getCoverageAmount());
        policy.setPremiumAmount(request.getPremiumAmount());
        policy.setDurationMonths(request.getDurationMonths());
        policy.setDescription(request.getDescription());
        policy.setIsActive(true);

        return policyRepository.save(policy);
    }

    public Policy updatePolicy(Long id, PolicyRequest request) {
        Policy policy = getPolicyById(id);
        policy.setName(request.getName());
        policy.setType(request.getType());
        policy.setCoverageAmount(request.getCoverageAmount());
        policy.setPremiumAmount(request.getPremiumAmount());
        policy.setDurationMonths(request.getDurationMonths());
        policy.setDescription(request.getDescription());

        return policyRepository.save(policy);
    }

    public void deletePolicy(Long id) {
        Policy policy = getPolicyById(id);
        policy.setIsActive(false);
        policyRepository.save(policy);
    }

    public Policy activatePolicy(Long id) {
        Policy policy = getPolicyById(id);
        policy.setIsActive(true);
        return policyRepository.save(policy);
    }
}
