package com.virul.medisure.service;

import com.virul.medisure.model.Payment;
import com.virul.medisure.model.Policy;
import com.virul.medisure.model.PolicyHolder;
import com.virul.medisure.model.User;
import com.virul.medisure.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment createPayment(User user, Policy policy, PolicyHolder policyHolder, 
                                BigDecimal amount, Payment.PaymentMethod paymentMethod, 
                                String paymentType) {
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPolicy(policy);
        payment.setPolicyHolder(policyHolder);
        payment.setTransactionId("TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setAmount(amount);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentType(paymentType);
        payment.setDescription("Payment for " + policy.getName());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }

    public Payment processDummyPayment(Payment payment) {
        // Simulate payment processing
        try {
            Thread.sleep(500); // Simulate processing delay
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setUpdatedAt(LocalDateTime.now());
            return paymentRepository.save(payment);
        } catch (InterruptedException e) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setUpdatedAt(LocalDateTime.now());
            return paymentRepository.save(payment);
        }
    }

    public Payment updatePaymentStatus(Long paymentId, Payment.PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    public List<Payment> getUserPayments(User user) {
        return paymentRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}

