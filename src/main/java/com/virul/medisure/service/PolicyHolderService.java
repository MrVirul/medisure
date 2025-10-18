package com.virul.medisure.service;

import com.virul.medisure.model.Policy;
import com.virul.medisure.model.PolicyHolder;
import com.virul.medisure.model.User;
import com.virul.medisure.repository.PolicyHolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyHolderService {

    private final PolicyHolderRepository policyHolderRepository;
    private final UserService userService;
    private final PolicyService policyService;

    public PolicyHolder purchasePolicy(Long userId, Long policyId) {
        User user = userService.getUserById(userId);
        Policy policy = policyService.getPolicyById(policyId);

        // Check if user already has an active policy
        if (hasActivePolicy(user)) {
            throw new RuntimeException("User already has an active policy");
        }

        PolicyHolder policyHolder = new PolicyHolder();
        policyHolder.setUser(user);
        policyHolder.setPolicy(policy);
        policyHolder.setStartDate(LocalDate.now());
        policyHolder.setEndDate(LocalDate.now().plusMonths(policy.getDurationMonths()));
        policyHolder.setStatus(PolicyHolder.PolicyStatus.ACTIVE);

        // Change user role from USER to POLICY_HOLDER
        userService.changeUserRole(userId, User.UserRole.POLICY_HOLDER);

        return policyHolderRepository.save(policyHolder);
    }

    public List<PolicyHolder> getAllPolicyHolders() {
        return policyHolderRepository.findAll();
    }

    public PolicyHolder getPolicyHolderByUser(User user) {
        return policyHolderRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Policy holder not found"));
    }

    public List<PolicyHolder> getPolicyHoldersByStatus(PolicyHolder.PolicyStatus status) {
        return policyHolderRepository.findByStatus(status);
    }

    public boolean hasActivePolicy(User user) {
        return policyHolderRepository.findByUser(user)
                .map(ph -> ph.getStatus() == PolicyHolder.PolicyStatus.ACTIVE)
                .orElse(false);
    }

    public PolicyHolder getPolicyHolderById(Long id) {
        return policyHolderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy holder not found"));
    }

    public PolicyHolder updatePolicyStatus(Long id, PolicyHolder.PolicyStatus status) {
        PolicyHolder policyHolder = getPolicyHolderById(id);
        policyHolder.setStatus(status);
        return policyHolderRepository.save(policyHolder);
    }
}
