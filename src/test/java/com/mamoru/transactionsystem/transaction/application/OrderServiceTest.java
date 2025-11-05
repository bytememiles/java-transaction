package com.mamoru.transactionsystem.transaction.application;

import com.mamoru.transactionsystem.common.exception.InvalidOperationException;
import com.mamoru.transactionsystem.merchant.application.InventoryService;
import com.mamoru.transactionsystem.merchant.application.MerchantService;
import com.mamoru.transactionsystem.merchant.application.ProductService;
import com.mamoru.transactionsystem.merchant.domain.Inventory;
import com.mamoru.transactionsystem.merchant.domain.Merchant;
import com.mamoru.transactionsystem.merchant.domain.Product;
import com.mamoru.transactionsystem.transaction.domain.Order;
import com.mamoru.transactionsystem.transaction.domain.OrderStatus;
import com.mamoru.transactionsystem.transaction.infrastructure.repository.OrderRepository;
import com.mamoru.transactionsystem.transaction.infrastructure.repository.PaymentRepository;
import com.mamoru.transactionsystem.user.application.AccountService;
import com.mamoru.transactionsystem.user.domain.Account;
import com.mamoru.transactionsystem.user.domain.User;
import com.mamoru.transactionsystem.user.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private PaymentRepository paymentRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ProductService productService;
    
    @Mock
    private InventoryService inventoryService;
    
    @Mock
    private MerchantService merchantService;
    
    @Mock
    private AccountService accountService;
    
    @InjectMocks
    private OrderService orderService;
    
    private User user;
    private Merchant merchant;
    private Product product;
    private Inventory inventory;
    private Account account;
    
    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("testuser").build();
        account = Account.builder()
                .id(1L)
                .user(user)
                .balance(BigDecimal.valueOf(100.00))
                .currency("USD")
                .build();
        merchant = Merchant.builder().id(1L).name("Test Merchant").build();
        product = Product.builder()
                .id(1L)
                .merchant(merchant)
                .sku("TEST-001")
                .name("Test Product")
                .price(BigDecimal.valueOf(10.00))
                .build();
        inventory = Inventory.builder()
                .id(1L)
                .product(product)
                .quantity(100)
                .build();
    }
    
    @Test
    void testProcessOrder_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productService.getProductByMerchantIdAndSku(1L, "TEST-001")).thenReturn(product);
        when(inventoryService.getInventoryByProductId(1L)).thenReturn(inventory);
        when(accountService.getBalanceByUserId(1L)).thenReturn(BigDecimal.valueOf(100.00));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            order.setOrderNumber("ORD-001");
            return order;
        });
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        when(accountService.debitAccount(anyLong(), any(), anyString())).thenReturn(account);
        doReturn(merchant).when(merchantService).creditMerchantAccount(anyLong(), any());
        doReturn(inventory).when(inventoryService).deductInventory(anyLong(), anyInt(), anyString());
        
        Order order = orderService.processOrder(1L, 1L, "TEST-001", 5);
        
        assertNotNull(order);
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(accountService, times(1)).debitAccount(anyLong(), any(), anyString());
        verify(merchantService, times(1)).creditMerchantAccount(anyLong(), any());
        verify(inventoryService, times(1)).deductInventory(anyLong(), anyInt(), anyString());
    }
    
    @Test
    void testProcessOrder_InsufficientStock() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productService.getProductByMerchantIdAndSku(1L, "TEST-001")).thenReturn(product);
        
        Inventory lowInventory = Inventory.builder()
                .id(1L)
                .product(product)
                .quantity(3)
                .build();
        when(inventoryService.getInventoryByProductId(1L)).thenReturn(lowInventory);
        
        assertThrows(InvalidOperationException.class, 
                () -> orderService.processOrder(1L, 1L, "TEST-001", 5));
    }
    
    @Test
    void testProcessOrder_InsufficientBalance() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productService.getProductByMerchantIdAndSku(1L, "TEST-001")).thenReturn(product);
        when(inventoryService.getInventoryByProductId(1L)).thenReturn(inventory);
        when(accountService.getBalanceByUserId(1L)).thenReturn(BigDecimal.valueOf(10.00)); // Low balance
        
        assertThrows(InvalidOperationException.class, 
                () -> orderService.processOrder(1L, 1L, "TEST-001", 5));
    }
}

