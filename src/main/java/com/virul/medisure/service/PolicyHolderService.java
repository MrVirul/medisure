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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PolicyHolderService {

    private static final String POLICY_HOLDER_NOT_FOUND = "Policy holder not found";
    private static final String POLICY_SUFFIX = ". Policy: ";

    private final PolicyHolderRepository policyHolderRepository;
    private final UserService userService;
    private final PolicyService policyService;
    private final PaymentService paymentService;
    private final AuditLogService auditLogService;

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
            policyHolder.setStatus(PolicyHolder.PolicyStatus.PENDING_APPROVAL);
            policyHolder.setPolicyManagerApproved(false);
            policyHolder.setFinanceOfficerApproved(false);

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

    /**
     * Deactivate/Cancel a specific policy holder's active policy
     */
    public PolicyHolder deactivatePolicy(Long policyHolderId, String reason, String performedBy) {
        PolicyHolder policyHolder = getPolicyHolderById(policyHolderId);
        
        // Only deactivate if currently active
        if (policyHolder.getStatus() != PolicyHolder.PolicyStatus.ACTIVE) {
            throw new IllegalStateException("Policy is not active. Current status: " + policyHolder.getStatus());
        }
        
        // Update status to CANCELLED
        policyHolder.setStatus(PolicyHolder.PolicyStatus.CANCELLED);
        policyHolder.setUpdatedAt(LocalDate.now().atStartOfDay());
        PolicyHolder updatedPolicyHolder = policyHolderRepository.save(policyHolder);
        
        // Log the deactivation action
        auditLogService.logAction(
            com.virul.medisure.model.AuditLog.EntityType.POLICY_HOLDER,
            policyHolderId,
            com.virul.medisure.model.AuditLog.Action.UPDATE,
            performedBy,
            "Policy deactivated. Reason: " + (reason != null ? reason : "No reason provided") + 
            POLICY_SUFFIX + policyHolder.getPolicy().getName()
        );
        
        return updatedPolicyHolder;
    }

    /**
     * Deactivate all expired policies
     */
    public List<PolicyHolder> deactivateExpiredPolicies(String performedBy) {
        List<PolicyHolder> activePolicyHolders = policyHolderRepository.findByStatus(PolicyHolder.PolicyStatus.ACTIVE);
        List<PolicyHolder> expiredPolicies = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (PolicyHolder policyHolder : activePolicyHolders) {
            if (policyHolder.getEndDate().isBefore(today) || policyHolder.getEndDate().isEqual(today)) {
                policyHolder.setStatus(PolicyHolder.PolicyStatus.EXPIRED);
                policyHolder.setUpdatedAt(LocalDate.now().atStartOfDay());
                PolicyHolder updated = policyHolderRepository.save(policyHolder);
                expiredPolicies.add(updated);
                
                // Log the expiration
                auditLogService.logAction(
                    com.virul.medisure.model.AuditLog.EntityType.POLICY_HOLDER,
                    policyHolder.getId(),
                    com.virul.medisure.model.AuditLog.Action.UPDATE,
                    performedBy,
                    "Policy automatically expired. End date: " + policyHolder.getEndDate() + 
                    POLICY_SUFFIX + policyHolder.getPolicy().getName()
                );
            }
        }
        
        return expiredPolicies;
    }

    /**
     * Suspend a policy holder's policy (temporary deactivation)
     */
    public PolicyHolder suspendPolicy(Long policyHolderId, String reason, String performedBy) {
        PolicyHolder policyHolder = getPolicyHolderById(policyHolderId);
        
        if (policyHolder.getStatus() != PolicyHolder.PolicyStatus.ACTIVE) {
            throw new IllegalStateException("Only active policies can be suspended. Current status: " + policyHolder.getStatus());
        }
        
        policyHolder.setStatus(PolicyHolder.PolicyStatus.SUSPENDED);
        policyHolder.setUpdatedAt(LocalDate.now().atStartOfDay());
        PolicyHolder updatedPolicyHolder = policyHolderRepository.save(policyHolder);
        
        // Log the suspension
        auditLogService.logAction(
            com.virul.medisure.model.AuditLog.EntityType.POLICY_HOLDER,
            policyHolderId,
            com.virul.medisure.model.AuditLog.Action.UPDATE,
            performedBy,
            "Policy suspended. Reason: " + (reason != null ? reason : "No reason provided") + 
            POLICY_SUFFIX + policyHolder.getPolicy().getName()
        );
        
        return updatedPolicyHolder;
    }

    /**
     * Reactivate a suspended policy
     */
    public PolicyHolder reactivatePolicy(Long policyHolderId, String performedBy) {
        PolicyHolder policyHolder = getPolicyHolderById(policyHolderId);
        
        if (policyHolder.getStatus() != PolicyHolder.PolicyStatus.SUSPENDED) {
            throw new IllegalStateException("Only suspended policies can be reactivated. Current status: " + policyHolder.getStatus());
        }
        
        // Check if policy hasn't expired
        if (policyHolder.getEndDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot reactivate an expired policy. End date was: " + policyHolder.getEndDate());
        }
        
        policyHolder.setStatus(PolicyHolder.PolicyStatus.ACTIVE);
        policyHolder.setUpdatedAt(LocalDate.now().atStartOfDay());
        PolicyHolder updatedPolicyHolder = policyHolderRepository.save(policyHolder);
        
        // Log the reactivation
        auditLogService.logAction(
            com.virul.medisure.model.AuditLog.EntityType.POLICY_HOLDER,
            policyHolderId,
            com.virul.medisure.model.AuditLog.Action.UPDATE,
            performedBy,
            "Policy reactivated from suspended status" + POLICY_SUFFIX + policyHolder.getPolicy().getName()
        );
        
        return updatedPolicyHolder;
    }

    /**
     * Get count of active policies
     */
    public long getActivePolicyCount() {
        return policyHolderRepository.findByStatus(PolicyHolder.PolicyStatus.ACTIVE).size();
    }

    /**
     * Get count of policies by status
     */
    public Map<PolicyHolder.PolicyStatus, Long> getPolicyCountByStatus() {
        Map<PolicyHolder.PolicyStatus, Long> counts = new EnumMap<>(PolicyHolder.PolicyStatus.class);
        for (PolicyHolder.PolicyStatus status : PolicyHolder.PolicyStatus.values()) {
            long count = policyHolderRepository.findByStatus(status).size();
            counts.put(status, count);
        }
        return counts;
    }

    /**
     * Policy Manager approval for a policy
     */
    public PolicyHolder approvePolicyByPolicyManager(Long policyHolderId, String approvedBy) {
        PolicyHolder policyHolder = getPolicyHolderById(policyHolderId);
        
        if (policyHolder.getStatus() != PolicyHolder.PolicyStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Policy is not in pending approval status. Current status: " + policyHolder.getStatus());
        }
        
        if (Boolean.TRUE.equals(policyHolder.getPolicyManagerApproved())) {
            throw new IllegalStateException("Policy has already been approved by Policy Manager");
        }
        
        policyHolder.setPolicyManagerApproved(true);
        policyHolder.setPolicyManagerApprovedBy(approvedBy);
        policyHolder.setPolicyManagerApprovedAt(LocalDate.now().atStartOfDay());
        policyHolder.setUpdatedAt(LocalDate.now().atStartOfDay());
        
        // Check if both approvals are done
        if (Boolean.TRUE.equals(policyHolder.getFinanceOfficerApproved())) {
            policyHolder.setStatus(PolicyHolder.PolicyStatus.ACTIVE);
        }
        
        PolicyHolder updatedPolicyHolder = policyHolderRepository.save(policyHolder);
        
        // Log the approval
        auditLogService.logAction(
            com.virul.medisure.model.AuditLog.EntityType.POLICY_HOLDER,
            policyHolderId,
            com.virul.medisure.model.AuditLog.Action.UPDATE,
            approvedBy,
            "Policy approved by Policy Manager" + POLICY_SUFFIX + policyHolder.getPolicy().getName()
        );
        
        return updatedPolicyHolder;
    }

    /**
     * Finance Officer approval for a policy
     */
    public PolicyHolder approvePolicyByFinanceOfficer(Long policyHolderId, String approvedBy) {
        PolicyHolder policyHolder = getPolicyHolderById(policyHolderId);
        
        if (policyHolder.getStatus() != PolicyHolder.PolicyStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Policy is not in pending approval status. Current status: " + policyHolder.getStatus());
        }
        
        if (Boolean.TRUE.equals(policyHolder.getFinanceOfficerApproved())) {
            throw new IllegalStateException("Policy has already been approved by Finance Officer");
        }
        
        policyHolder.setFinanceOfficerApproved(true);
        policyHolder.setFinanceOfficerApprovedBy(approvedBy);
        policyHolder.setFinanceOfficerApprovedAt(LocalDate.now().atStartOfDay());
        policyHolder.setUpdatedAt(LocalDate.now().atStartOfDay());
        
        // Check if both approvals are done
        if (Boolean.TRUE.equals(policyHolder.getPolicyManagerApproved())) {
            policyHolder.setStatus(PolicyHolder.PolicyStatus.ACTIVE);
        }
        
        PolicyHolder updatedPolicyHolder = policyHolderRepository.save(policyHolder);
        
        // Log the approval
        auditLogService.logAction(
            com.virul.medisure.model.AuditLog.EntityType.POLICY_HOLDER,
            policyHolderId,
            com.virul.medisure.model.AuditLog.Action.UPDATE,
            approvedBy,
            "Policy approved by Finance Officer" + POLICY_SUFFIX + policyHolder.getPolicy().getName()
        );
        
        return updatedPolicyHolder;
    }

    /**
     * Get all policies pending approval
     */
    public List<PolicyHolder> getPendingApprovalPolicies() {
        return policyHolderRepository.findByStatus(PolicyHolder.PolicyStatus.PENDING_APPROVAL);
    }
}
