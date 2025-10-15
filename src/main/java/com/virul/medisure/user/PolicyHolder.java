package com.virul.medisure.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "policy_holders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PolicyHolder extends User {
    
    @Column(name = "policy_number", unique = true)
    private String policyNumber;
    
    @Column(name = "coverage_amount")
    private BigDecimal coverageAmount;
    
    @Column(name = "premium_amount")
    private BigDecimal premiumAmount;
}