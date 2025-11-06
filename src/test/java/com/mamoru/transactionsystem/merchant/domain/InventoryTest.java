package com.mamoru.transactionsystem.merchant.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {
    
    private static final UUID MERCHANT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID PRODUCT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final UUID INVENTORY_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    
    private Inventory inventory;
    private Product product;
    
    @BeforeEach
    void setUp() {
        Merchant merchant = Merchant.builder()
                .id(MERCHANT_ID)
                .name("Test Merchant")
                .build();
        
        product = Product.builder()
                .id(PRODUCT_ID)
                .merchant(merchant)
                .sku("TEST-001")
                .name("Test Product")
                .price(java.math.BigDecimal.valueOf(10.00))
                .build();
        
        inventory = Inventory.builder()
                .id(INVENTORY_ID)
                .product(product)
                .quantity(100)
                .version(0L)
                .build();
    }
    
    @Test
    void testAddQuantity_Success() {
        Integer initialQuantity = inventory.getQuantity();
        Integer quantityToAdd = 50;
        
        inventory.addQuantity(quantityToAdd);
        
        assertEquals(initialQuantity + quantityToAdd, inventory.getQuantity());
    }
    
    @Test
    void testAddQuantity_ZeroAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> inventory.addQuantity(0));
        
        assertEquals("Quantity to add must be positive", exception.getMessage());
    }
    
    @Test
    void testAddQuantity_NullAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> inventory.addQuantity(null));
        
        assertEquals("Quantity to add must be positive", exception.getMessage());
    }
    
    @Test
    void testDeductQuantity_Success() {
        Integer initialQuantity = inventory.getQuantity();
        Integer quantityToDeduct = 30;
        
        inventory.deductQuantity(quantityToDeduct);
        
        assertEquals(initialQuantity - quantityToDeduct, inventory.getQuantity());
    }
    
    @Test
    void testDeductQuantity_InsufficientStock() {
        Integer quantityToDeduct = 150;
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> inventory.deductQuantity(quantityToDeduct));
        
        assertTrue(exception.getMessage().contains("Insufficient stock"));
        assertEquals(100, inventory.getQuantity());
    }
    
    @Test
    void testHasSufficientStock_True() {
        assertTrue(inventory.hasSufficientStock(50));
        assertTrue(inventory.hasSufficientStock(100));
    }
    
    @Test
    void testHasSufficientStock_False() {
        assertFalse(inventory.hasSufficientStock(101));
        assertFalse(inventory.hasSufficientStock(150));
    }
    
    @Test
    void testHasSufficientStock_Null() {
        assertFalse(inventory.hasSufficientStock(null));
    }
}

