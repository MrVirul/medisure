package com.virul.medisure.service;

import com.virul.medisure.model.PolicyDocument;
import com.virul.medisure.model.PolicyHolder;
import com.virul.medisure.repository.PolicyDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyDocumentService {

    private final PolicyDocumentRepository policyDocumentRepository;

    /**
     * Save a policy document record to database
     */
    public PolicyDocument saveDocument(PolicyHolder policyHolder, String fileName, String fileUrl, 
                                      PolicyDocument.DocumentType documentType, String description) {
        PolicyDocument document = new PolicyDocument();
        document.setPolicyHolder(policyHolder);
        document.setFileName(fileName);
        document.setFileUrl(fileUrl);
        document.setFileType("application/pdf");
        document.setDocumentType(documentType);
        document.setDescription(description);
        
        // Calculate file size
        try {
            File file = new File(fileUrl);
            if (file.exists()) {
                document.setFileSize(file.length());
            }
        } catch (Exception e) {
            // File size calculation failed, continue without it
        }
        
        return policyDocumentRepository.save(document);
    }

    /**
     * Get all documents for a policy holder
     */
    public List<PolicyDocument> getDocumentsByPolicyHolder(PolicyHolder policyHolder) {
        return policyDocumentRepository.findByPolicyHolderOrderByUploadedAtDesc(policyHolder);
    }

    /**
     * Get all documents for a policy holder by ID
     */
    public List<PolicyDocument> getDocumentsByPolicyHolderId(Long policyHolderId) {
        return policyDocumentRepository.findByPolicyHolder_IdOrderByUploadedAtDesc(policyHolderId);
    }

    /**
     * Get a specific document by ID
     */
    public PolicyDocument getDocumentById(Long id) {
        return policyDocumentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Document not found"));
    }

    /**
     * Get document as downloadable resource
     */
    public Resource getDocumentAsResource(PolicyDocument document) {
        try {
            Path filePath = Paths.get(document.getFileUrl()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new IllegalStateException("Document file not found or not readable");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error loading document file", e);
        }
    }

    /**
     * Delete a document
     */
    public void deleteDocument(Long id) {
        PolicyDocument document = getDocumentById(id);
        
        // Delete physical file
        try {
            File file = new File(document.getFileUrl());
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            // Continue even if file deletion fails
        }
        
        // Delete database record
        policyDocumentRepository.delete(document);
    }

    /**
     * Get all documents
     */
    public List<PolicyDocument> getAllDocuments() {
        return policyDocumentRepository.findAll();
    }
}

