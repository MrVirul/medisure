package com.virul.medisure.controller;

import com.virul.medisure.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    @PostMapping("/dummy")
    public ResponseEntity<ApiResponse<Map<String, String>>> dummyPayment(@RequestBody Map<String, Object> request) {
        // Simulate payment processing
        try {
            Thread.sleep(1000); // Simulate processing delay
            
            Map<String, String> response = Map.of(
                "transactionId", "TXN" + System.currentTimeMillis(),
                "status", "SUCCESS",
                "message", "Payment processed successfully"
            );
            
            return ResponseEntity.ok(ApiResponse.success("Payment successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Payment failed"));
        }
    }
}
