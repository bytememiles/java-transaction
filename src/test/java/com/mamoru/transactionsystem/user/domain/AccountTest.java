package com.mamoru.transactionsystem.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    
    private Account account;
    private User user;
    
    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();
        
        account = Account.builder()
                .id(1L)
                .user(user)
                .balance(BigDecimal.valueOf(100.00))
                .currency("USD")
                .version(0L)
                .build();
    }
    
    @Test
    void testDebit_Success() {
        BigDecimal initialBalance = account.getBalance();
        BigDecimal debitAmount = BigDecimal.valueOf(50.00);
        
        account.debit(debitAmount);
        
        assertEquals(initialBalance.subtract(debitAmount), account.getBalance());
    }
    
    @Test
    void testDebit_InsufficientBalance() {
        BigDecimal debitAmount = BigDecimal.valueOf(150.00);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> account.debit(debitAmount));
        
        assertEquals("Insufficient balance", exception.getMessage());
        assertEquals(BigDecimal.valueOf(100.00), account.getBalance());
    }
    
    @Test
    void testDebit_ZeroAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> account.debit(BigDecimal.ZERO));
        
        assertEquals("Debit amount must be positive", exception.getMessage());
    }
    
    @Test
    void testDebit_NegativeAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> account.debit(BigDecimal.valueOf(-10.00)));
        
        assertEquals("Debit amount must be positive", exception.getMessage());
    }
    
    @Test
    void testCredit_Success() {
        BigDecimal initialBalance = account.getBalance();
        BigDecimal creditAmount = BigDecimal.valueOf(25.00);
        
        account.credit(creditAmount);
        
        assertEquals(initialBalance.add(creditAmount), account.getBalance());
    }
    
    @Test
    void testCredit_ZeroAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> account.credit(BigDecimal.ZERO));
        
        assertEquals("Credit amount must be positive", exception.getMessage());
    }
    
    @Test
    void testCredit_NegativeAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> account.credit(BigDecimal.valueOf(-10.00)));
        
        assertEquals("Credit amount must be positive", exception.getMessage());
    }
}

