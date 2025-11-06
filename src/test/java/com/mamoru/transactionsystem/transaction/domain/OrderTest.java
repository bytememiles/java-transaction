package com.mamoru.transactionsystem.transaction.domain;

import com.mamoru.transactionsystem.merchant.domain.Merchant;
import com.mamoru.transactionsystem.merchant.domain.Product;
import com.mamoru.transactionsystem.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    
    private static final UUID USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID MERCHANT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final UUID PRODUCT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    private static final UUID ORDER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");
    
    private Order order;
    
    @BeforeEach
    void setUp() {
        User user = User.builder().id(USER_ID).username("testuser").build();
        Merchant merchant = Merchant.builder().id(MERCHANT_ID).name("Test Merchant").build();
        Product product = Product.builder().id(PRODUCT_ID).merchant(merchant).sku("TEST-001").build();
        
        order = Order.builder()
                .id(ORDER_ID)
                .orderNumber("ORD-001")
                .user(user)
                .product(product)
                .merchant(merchant)
                .sku("TEST-001")
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(10.00))
                .totalAmount(BigDecimal.valueOf(50.00))
                .status(OrderStatus.PENDING)
                .build();
    }
    
    @Test
    void testMarkAsCompleted_Success() {
        order.markAsCompleted();
        
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }
    
    @Test
    void testMarkAsCompleted_FromNonPending() {
        order.markAsCompleted();
        order.setStatus(OrderStatus.FAILED);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
                () -> order.markAsCompleted());
        
        assertTrue(exception.getMessage().contains("Cannot complete order"));
    }
    
    @Test
    void testMarkAsFailed_Success() {
        order.markAsFailed();
        
        assertEquals(OrderStatus.FAILED, order.getStatus());
    }
    
    @Test
    void testMarkAsFailed_FromCompleted() {
        order.markAsCompleted();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
                () -> order.markAsFailed());
        
        assertEquals("Cannot fail a completed order", exception.getMessage());
    }
    
    @Test
    void testMarkAsRefunded_Success() {
        order.markAsCompleted();
        order.markAsRefunded();
        
        assertEquals(OrderStatus.REFUNDED, order.getStatus());
    }
    
    @Test
    void testMarkAsRefunded_FromNonCompleted() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
                () -> order.markAsRefunded());
        
        assertTrue(exception.getMessage().contains("Cannot refund order"));
    }
    
    @Test
    void testIsInTerminalState_Completed() {
        order.markAsCompleted();
        assertTrue(order.isInTerminalState());
    }
    
    @Test
    void testIsInTerminalState_Failed() {
        order.markAsFailed();
        assertTrue(order.isInTerminalState());
    }
    
    @Test
    void testIsInTerminalState_Refunded() {
        order.markAsCompleted();
        order.markAsRefunded();
        assertTrue(order.isInTerminalState());
    }
    
    @Test
    void testIsInTerminalState_Pending() {
        assertFalse(order.isInTerminalState());
    }
}

