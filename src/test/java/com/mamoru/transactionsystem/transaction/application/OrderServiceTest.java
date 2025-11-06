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
import java.util.UUID;

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
    
    private static final UUID USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID MERCHANT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final UUID PRODUCT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    private static final UUID ACCOUNT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");
    private static final UUID INVENTORY_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440004");
    private static final UUID ORDER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440005");
    
    private User user;
    private Merchant merchant;
    private Product product;
    private Inventory inventory;
    private Account account;
    
    @BeforeEach
    void setUp() {
        user = User.builder().id(USER_ID).username("testuser").build();
        account = Account.builder()
                .id(ACCOUNT_ID)
                .user(user)
                .balance(BigDecimal.valueOf(100.00))
                .currency("USD")
                .build();
        merchant = Merchant.builder().id(MERCHANT_ID).name("Test Merchant").build();
        product = Product.builder()
                .id(PRODUCT_ID)
                .merchant(merchant)
                .sku("TEST-001")
                .name("Test Product")
                .price(BigDecimal.valueOf(10.00))
                .build();
        inventory = Inventory.builder()
                .id(INVENTORY_ID)
                .product(product)
                .quantity(100)
                .build();
    }
    
    @Test
    void testProcessOrder_Success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(productService.getProductByMerchantIdAndSku(MERCHANT_ID, "TEST-001")).thenReturn(product);
        when(inventoryService.getInventoryByProductId(PRODUCT_ID)).thenReturn(inventory);
        when(accountService.getBalanceByUserId(USER_ID)).thenReturn(BigDecimal.valueOf(100.00));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(ORDER_ID);
            order.setOrderNumber("ORD-001");
            return order;
        });
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        when(accountService.debitAccount(any(UUID.class), any(), anyString())).thenReturn(account);
        doReturn(merchant).when(merchantService).creditMerchantAccount(any(UUID.class), any());
        doReturn(inventory).when(inventoryService).deductInventory(any(UUID.class), anyInt(), anyString());
        
        Order order = orderService.processOrder(USER_ID, MERCHANT_ID, "TEST-001", 5);
        
        assertNotNull(order);
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(accountService, times(1)).debitAccount(any(UUID.class), any(), anyString());
        verify(merchantService, times(1)).creditMerchantAccount(any(UUID.class), any());
        verify(inventoryService, times(1)).deductInventory(any(UUID.class), anyInt(), anyString());
    }
    
    @Test
    void testProcessOrder_InsufficientStock() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(productService.getProductByMerchantIdAndSku(MERCHANT_ID, "TEST-001")).thenReturn(product);
        
        Inventory lowInventory = Inventory.builder()
                .id(INVENTORY_ID)
                .product(product)
                .quantity(3)
                .build();
        when(inventoryService.getInventoryByProductId(PRODUCT_ID)).thenReturn(lowInventory);
        
        assertThrows(InvalidOperationException.class, 
                () -> orderService.processOrder(USER_ID, MERCHANT_ID, "TEST-001", 5));
    }
    
    @Test
    void testProcessOrder_InsufficientBalance() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(productService.getProductByMerchantIdAndSku(MERCHANT_ID, "TEST-001")).thenReturn(product);
        when(inventoryService.getInventoryByProductId(PRODUCT_ID)).thenReturn(inventory);
        when(accountService.getBalanceByUserId(USER_ID)).thenReturn(BigDecimal.valueOf(10.00)); // Low balance
        
        assertThrows(InvalidOperationException.class, 
                () -> orderService.processOrder(USER_ID, MERCHANT_ID, "TEST-001", 5));
    }
}

