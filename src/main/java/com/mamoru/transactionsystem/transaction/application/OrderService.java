package com.mamoru.transactionsystem.transaction.application;

import com.mamoru.transactionsystem.common.exception.InvalidOperationException;
import com.mamoru.transactionsystem.common.exception.ResourceNotFoundException;
import com.mamoru.transactionsystem.merchant.application.InventoryService;
import com.mamoru.transactionsystem.merchant.application.MerchantService;
import com.mamoru.transactionsystem.merchant.application.ProductService;
import com.mamoru.transactionsystem.merchant.domain.Inventory;
import com.mamoru.transactionsystem.merchant.domain.Product;
import com.mamoru.transactionsystem.transaction.domain.Order;
import com.mamoru.transactionsystem.transaction.domain.OrderStatus;
import com.mamoru.transactionsystem.transaction.domain.Payment;
import com.mamoru.transactionsystem.transaction.domain.PaymentStatus;
import com.mamoru.transactionsystem.transaction.infrastructure.repository.OrderRepository;
import com.mamoru.transactionsystem.transaction.infrastructure.repository.PaymentRepository;
import com.mamoru.transactionsystem.user.application.AccountService;
import com.mamoru.transactionsystem.user.domain.User;
import com.mamoru.transactionsystem.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final MerchantService merchantService;
    private final AccountService accountService;
    
    @Transactional
    public Order processOrder(UUID userId, UUID merchantId, String sku, Integer quantity) {
        log.info("Processing order for user ID: {}, merchant ID: {}, SKU: {}, quantity: {}", 
                userId, merchantId, sku, quantity);
        
        // 1. Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // 2. Validate product exists and get details
        Product product = productService.getProductByMerchantIdAndSku(merchantId, sku);
        
        // 3. Validate inventory has sufficient stock
        Inventory inventory = inventoryService.getInventoryByProductId(product.getId());
        if (!inventory.hasSufficientStock(quantity)) {
            throw new InvalidOperationException(
                String.format("Insufficient stock. Available: %d, Requested: %d", 
                    inventory.getQuantity(), quantity));
        }
        
        // 4. Calculate total amount
        BigDecimal unitPrice = product.getPrice();
        BigDecimal totalAmount = product.calculateTotalPrice(quantity);
        
        // 5. Check user account has sufficient balance
        BigDecimal currentBalance = accountService.getBalanceByUserId(userId);
        if (currentBalance.compareTo(totalAmount) < 0) {
            throw new InvalidOperationException(
                String.format("Insufficient balance. Available: %s, Required: %s", 
                    currentBalance, totalAmount));
        }
        
        // 6. Create order
        String orderNumber = generateOrderNumber();
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .user(user)
                .product(product)
                .merchant(product.getMerchant())
                .sku(sku)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .build();
        order = orderRepository.save(order);
        
        try {
            // 7. Process payment: Deduct from user account
            accountService.debitAccount(userId, totalAmount, orderNumber);
            
            // 8. Credit merchant account
            merchantService.creditMerchantAccount(merchantId, totalAmount);
            
            // 9. Deduct inventory
            inventoryService.deductInventory(product.getId(), quantity, orderNumber);
            
            // 10. Create payment record
            Payment payment = Payment.builder()
                    .order(order)
                    .paymentMethod("PREPAID_ACCOUNT")
                    .amount(totalAmount)
                    .status(PaymentStatus.COMPLETED)
                    .transactionId("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .build();
            paymentRepository.save(payment);
            
            // 11. Mark order as completed
            order.markAsCompleted();
            order = orderRepository.save(order);
            
            log.info("Order processed successfully. Order number: {}", orderNumber);
            return order;
            
        } catch (Exception e) {
            log.error("Error processing order: {}", e.getMessage(), e);
            order.markAsFailed();
            order = orderRepository.save(order);
            
            // Create failed payment record
            Payment payment = Payment.builder()
                    .order(order)
                    .paymentMethod("PREPAID_ACCOUNT")
                    .amount(totalAmount)
                    .status(PaymentStatus.FAILED)
                    .build();
            paymentRepository.save(payment);
            
            throw new RuntimeException("Order processing failed: " + e.getMessage(), e);
        }
    }
    
    @Transactional(readOnly = true)
    public Order getOrderById(UUID orderId) {
        log.debug("Fetching order by ID: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }
    
    @Transactional(readOnly = true)
    public Order getOrderByOrderNumber(String orderNumber) {
        log.debug("Fetching order by order number: {}", orderNumber);
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
    }
    
    private String generateOrderNumber() {
        return "ORD-" + LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
                + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}

