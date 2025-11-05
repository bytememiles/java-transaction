package com.mamoru.transactionsystem.merchant.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    
    private Product product;
    
    @BeforeEach
    void setUp() {
        Merchant merchant = Merchant.builder()
                .id(1L)
                .name("Test Merchant")
                .build();
        
        product = Product.builder()
                .id(1L)
                .merchant(merchant)
                .sku("TEST-001")
                .name("Test Product")
                .price(BigDecimal.valueOf(10.50))
                .currency("USD")
                .build();
    }
    
    @Test
    void testCalculateTotalPrice_Success() {
        Integer quantity = 5;
        BigDecimal expectedTotal = BigDecimal.valueOf(52.50); // 10.50 * 5
        
        BigDecimal result = product.calculateTotalPrice(quantity);
        
        assertEquals(expectedTotal, result);
    }
    
    @Test
    void testCalculateTotalPrice_ZeroQuantity() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> product.calculateTotalPrice(0));
        
        assertEquals("Quantity must be positive", exception.getMessage());
    }
    
    @Test
    void testCalculateTotalPrice_NegativeQuantity() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> product.calculateTotalPrice(-5));
        
        assertEquals("Quantity must be positive", exception.getMessage());
    }
    
    @Test
    void testCalculateTotalPrice_NullQuantity() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> product.calculateTotalPrice(null));
        
        assertEquals("Quantity must be positive", exception.getMessage());
    }
}

