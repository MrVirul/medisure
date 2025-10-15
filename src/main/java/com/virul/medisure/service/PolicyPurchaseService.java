package com.virul.medisure.service;

import com.virul.medisure.policy.Policy;
import com.virul.medisure.policy.PolicyPurchase;
import com.virul.medisure.policy.PolicyPurchaseStatus;
import com.virul.medisure.repository.PolicyPurchaseRepository;
import com.virul.medisure.repository.PolicyRepository;
import com.virul.medisure.user.PolicyHolder;
import com.virul.medisure.repository.PolicyHolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PolicyPurchaseService {
    
    private final PolicyPurchaseRepository policyPurchaseRepository;
    private final PolicyRepository policyRepository;
    private final PolicyHolderRepository policyHolderRepository;
    
    public PolicyPurchase createPolicyPurchase(PolicyPurchase policyPurchase) {
        // Validate policy exists
        Policy policy = policyRepository.findById(policyPurchase.getPolicy().getId())
                .orElseThrow(() -> new RuntimeException("Policy not found with ID: " + policyPurchase.getPolicy().getId()));
        
        // Validate policy holder exists
        PolicyHolder policyHolder = policyHolderRepository.findById(policyPurchase.getPolicyHolder().getId())
                .orElseThrow(() -> new RuntimeException("Policy Holder not found with ID: " + policyPurchase.getPolicyHolder().getId()));
        
        policyPurchase.setPolicy(policy);
        policyPurchase.setPolicyHolder(policyHolder);
        
        return policyPurchaseRepository.save(policyPurchase);
    }
    
    public Optional<PolicyPurchase> getPolicyPurchaseById(Long id) {
        return policyPurchaseRepository.findById(id);
    }
    
    public Page<PolicyPurchase> getAllPolicyPurchases(Pageable pageable) {
        return policyPurchaseRepository.findAll(pageable);
    }
    
    public Page<PolicyPurchase> getPolicyPurchasesByStatus(PolicyPurchaseStatus status, Pageable pageable) {
        return policyPurchaseRepository.findByStatus(status, pageable);
    }
    
    public Page<PolicyPurchase> getPolicyPurchasesByPolicyHolder(Long policyHolderId, Pageable pageable) {
        return policyPurchaseRepository.findByPolicyHolderId(policyHolderId, pageable);
    }
    
    public Page<PolicyPurchase> getPolicyPurchasesByPolicy(Long policyId, Pageable pageable) {
        return policyPurchaseRepository.findByPolicyId(policyId, pageable);
    }
    
    public Optional<PolicyPurchase> updatePolicyPurchase(Long id, PolicyPurchase policyPurchaseDetails) {
        return policyPurchaseRepository.findById(id)
                .map(policyPurchase -> {
                    if (policyPurchaseDetails.getPurchaseDate() != null) {
                        policyPurchase.setPurchaseDate(policyPurchaseDetails.getPurchaseDate());
                    }
                    if (policyPurchaseDetails.getExpiryDate() != null) {
                        policyPurchase.setExpiryDate(policyPurchaseDetails.getExpiryDate());
                    }
                    if (policyPurchaseDetails.getStatus() != null) {
                        policyPurchase.setStatus(policyPurchaseDetails.getStatus());
                    }
                    if (policyPurchaseDetails.getDocumentPath() != null) {
                        policyPurchase.setDocumentPath(policyPurchaseDetails.getDocumentPath());
                    }
                    
                    return policyPurchaseRepository.save(policyPurchase);
                });
    }
    
    public boolean deletePolicyPurchase(Long id) {
        if (policyPurchaseRepository.existsById(id)) {
            policyPurchaseRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<PolicyPurchase> getExpiringPolicies(LocalDate date) {
        return policyPurchaseRepository.findExpiringPolicies(date);
    }
}