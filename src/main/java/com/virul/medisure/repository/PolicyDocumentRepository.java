package com.virul.medisure.repository;

import com.virul.medisure.model.PolicyDocument;
import com.virul.medisure.model.PolicyHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyDocumentRepository extends JpaRepository<PolicyDocument, Long> {
    List<PolicyDocument> findByPolicyHolder(PolicyHolder policyHolder);
    List<PolicyDocument> findByPolicyHolderOrderByUploadedAtDesc(PolicyHolder policyHolder);
    List<PolicyDocument> findByPolicyHolder_IdOrderByUploadedAtDesc(Long policyHolderId);
}

