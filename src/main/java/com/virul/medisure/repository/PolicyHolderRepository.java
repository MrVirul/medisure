package com.virul.medisure.repository;

import com.virul.medisure.model.PolicyHolder;
import com.virul.medisure.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyHolderRepository extends JpaRepository<PolicyHolder, Long> {
    Optional<PolicyHolder> findByUser(User user);
    List<PolicyHolder> findByUserRole(User.UserRole role);
    List<PolicyHolder> findByStatus(PolicyHolder.PolicyStatus status);
}
