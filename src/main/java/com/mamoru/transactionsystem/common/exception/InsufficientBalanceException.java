package com.mamoru.transactionsystem.common.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    
    private final BigDecimal currentBalance;
    private final BigDecimal requiredAmount;
    
    public InsufficientBalanceException(String message) {
        super(message);
        this.currentBalance = null;
        this.requiredAmount = null;
    }
    
    public InsufficientBalanceException(BigDecimal currentBalance, BigDecimal requiredAmount) {
        super(String.format("Insufficient balance. Current: %s, Required: %s", 
                currentBalance, requiredAmount));
        this.currentBalance = currentBalance;
        this.requiredAmount = requiredAmount;
    }
    
    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }
    
    public BigDecimal getRequiredAmount() {
        return requiredAmount;
    }
}

