package com.mamoru.transactionsystem.merchant.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MerchantTest {
    
    private static final UUID MERCHANT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    
    private Merchant merchant;
    
    @BeforeEach
    void setUp() {
        merchant = Merchant.builder()
                .id(MERCHANT_ID)
                .name("Test Merchant")
                .accountBalance(BigDecimal.valueOf(500.00))
                .currency("USD")
                .version(0L)
                .build();
    }
    
    @Test
    void testCredit_Success() {
        BigDecimal initialBalance = merchant.getAccountBalance();
        BigDecimal creditAmount = BigDecimal.valueOf(100.00);
        
        merchant.credit(creditAmount);
        
        assertEquals(initialBalance.add(creditAmount), merchant.getAccountBalance());
    }
    
    @Test
    void testCredit_ZeroAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> merchant.credit(BigDecimal.ZERO));
        
        assertEquals("Credit amount must be positive", exception.getMessage());
    }
    
    @Test
    void testDebit_Success() {
        BigDecimal initialBalance = merchant.getAccountBalance();
        BigDecimal debitAmount = BigDecimal.valueOf(200.00);
        
        merchant.debit(debitAmount);
        
        assertEquals(initialBalance.subtract(debitAmount), merchant.getAccountBalance());
    }
    
    @Test
    void testDebit_InsufficientBalance() {
        BigDecimal debitAmount = BigDecimal.valueOf(600.00);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> merchant.debit(debitAmount));
        
        assertEquals("Insufficient merchant account balance", exception.getMessage());
    }
}

