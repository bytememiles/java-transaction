package com.mamoru.transactionsystem.payment.gateway.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mocked Payment Gateway Service for account recharge.
 * In production, this would integrate with a real banking API.
 */
@Service
@Slf4j
public class PaymentGatewayService {
    
    /**
     * Processes a recharge request through the mocked payment gateway.
     * 
     * @param userId the user ID
     * @param amount the amount to recharge
     * @return transaction ID from the payment gateway
     */
    public String processRecharge(Long userId, BigDecimal amount) {
        log.info("Processing recharge through payment gateway for user ID: {}, amount: {}", userId, amount);
        
        // Mock payment gateway processing
        // In real implementation, this would call the banking API
        // For now, we simulate a successful transaction
        
        try {
            // Simulate API call delay
            Thread.sleep(100);
            
            // Generate mock transaction ID
            String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            log.info("Recharge processed successfully. Transaction ID: {}", transactionId);
            return transactionId;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment gateway processing interrupted", e);
            throw new RuntimeException("Payment gateway processing failed", e);
        }
    }
}

