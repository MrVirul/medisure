package com.virul.medisure.repository;

import com.virul.medisure.model.ClaimDocument;
import com.virul.medisure.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimDocumentRepository extends JpaRepository<ClaimDocument, Long> {
    List<ClaimDocument> findByClaim(Claim claim);
}
