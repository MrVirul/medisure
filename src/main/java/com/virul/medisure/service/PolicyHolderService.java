package com.virul.medisure.service;

import com.virul.medisure.model.Payment;
import com.virul.medisure.model.Policy;
import com.virul.medisure.model.PolicyHolder;
import com.virul.medisure.model.User;
import com.virul.medisure.repository.PolicyHolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PolicyHolderService {

    private static final String POLICY_HOLDER_NOT_FOUND = "Policy holder not found";

    private final PolicyHolderRepository policyHolderRepository;
    private final UserService userService;
    private final PolicyService policyService;
    private final PaymentService paymentService;

    public PolicyHolder purchasePolicy(Long userId, Long policyId) {
        User user = userService.getUserById(userId);
        Policy policy = policyService.getPolicyById(policyId);

        // Check if policy is active
        if (Boolean.FALSE.equals(policy.getIsActive())) {
            throw new IllegalStateException("This policy is not available for purchase");
        }

        // Check if user already has an active policy
        if (hasActivePolicy(user)) {
            // If user has active policy, upgrade/change it
            return upgradePolicy(user, policy);
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

    public Map<String, Object> purchasePolicyWithPayment(Long userId, Long policyId) {
        User user = userService.getUserById(userId);
        Policy policy = policyService.getPolicyById(policyId);

        // Check if policy is active
        if (Boolean.FALSE.equals(policy.getIsActive())) {
            throw new IllegalStateException("This policy is not available for purchase");
        }

        // Calculate total amount (premium * duration)
        BigDecimal totalAmount = policy.getPremiumAmount().multiply(new BigDecimal(policy.getDurationMonths()));

        // Create payment record
        Payment payment = paymentService.createPayment(
            user, 
            policy, 
            null, // PolicyHolder will be set after creation
            totalAmount, 
            Payment.PaymentMethod.DUMMY, 
            "POLICY_PURCHASE"
        );

        // Process dummy payment
        payment = paymentService.processDummyPayment(payment);

        // Check if payment was successful
        if (payment.getStatus() != Payment.PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Payment failed. Please try again.");
        }

        // Check if user already has an active policy
        PolicyHolder policyHolder;
        if (hasActivePolicy(user)) {
            // If user has active policy, upgrade/change it
            policyHolder = upgradePolicy(user, policy);
        } else {
            policyHolder = new PolicyHolder();
            policyHolder.setUser(user);
            policyHolder.setPolicy(policy);
            policyHolder.setStartDate(LocalDate.now());
            policyHolder.setEndDate(LocalDate.now().plusMonths(policy.getDurationMonths()));
            policyHolder.setStatus(PolicyHolder.PolicyStatus.ACTIVE);

            // Change user role from USER to POLICY_HOLDER
            userService.changeUserRole(userId, User.UserRole.POLICY_HOLDER);

            policyHolder = policyHolderRepository.save(policyHolder);
        }

        // Update payment with policy holder reference
        payment.setPolicyHolder(policyHolder);
        payment = paymentService.updatePaymentStatus(payment.getId(), Payment.PaymentStatus.SUCCESS);

        // Prepare result
        Map<String, Object> result = new HashMap<>();
        result.put("policyHolder", policyHolder);
        result.put("payment", payment);

        return result;
    }

    public PolicyHolder upgradePolicy(User user, Policy newPolicy) {
        PolicyHolder existingPolicyHolder = policyHolderRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException(POLICY_HOLDER_NOT_FOUND));

        // Update the policy
        existingPolicyHolder.setPolicy(newPolicy);
        existingPolicyHolder.setStartDate(LocalDate.now());
        existingPolicyHolder.setEndDate(LocalDate.now().plusMonths(newPolicy.getDurationMonths()));
        existingPolicyHolder.setStatus(PolicyHolder.PolicyStatus.ACTIVE);

        return policyHolderRepository.save(existingPolicyHolder);
    }

    public List<PolicyHolder> getAllPolicyHolders() {
        return policyHolderRepository.findAll();
    }

    public PolicyHolder getPolicyHolderByUser(User user) {
        return policyHolderRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException(POLICY_HOLDER_NOT_FOUND));
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
                .orElseThrow(() -> new IllegalStateException(POLICY_HOLDER_NOT_FOUND));
    }

    public PolicyHolder updatePolicyStatus(Long id, PolicyHolder.PolicyStatus status) {
        PolicyHolder policyHolder = getPolicyHolderById(id);
        policyHolder.setStatus(status);
        return policyHolderRepository.save(policyHolder);
    }
}
