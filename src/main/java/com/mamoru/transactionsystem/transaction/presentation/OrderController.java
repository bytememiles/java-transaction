package com.mamoru.transactionsystem.transaction.presentation;

import com.mamoru.transactionsystem.common.dto.ApiResponse;
import com.mamoru.transactionsystem.transaction.application.OrderService;
import com.mamoru.transactionsystem.transaction.domain.Order;
import com.mamoru.transactionsystem.transaction.presentation.dto.OrderRequest;
import com.mamoru.transactionsystem.transaction.presentation.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs for order processing and management")
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    @Operation(summary = "Place an order", description = "Places an order, deducts user balance, credits merchant, and deducts inventory. Requires X-User-Id header.")
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @Parameter(description = "User ID (required in header)", required = true) 
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody OrderRequest request) {
        log.info("Placing order for user ID: {}, merchant ID: {}, SKU: {}, quantity: {}", 
                userId, request.getMerchantId(), request.getSku(), request.getQuantity());
        
        Order order = orderService.processOrder(
                userId,
                request.getMerchantId(),
                request.getSku(),
                request.getQuantity());
        
        OrderResponse response = OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .productId(order.getProduct().getId())
                .merchantId(order.getMerchant().getId())
                .sku(order.getSku())
                .quantity(order.getQuantity())
                .unitPrice(order.getUnitPrice())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", response));
    }
    
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieves order details by order ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable UUID orderId) {
        log.info("Fetching order with ID: {}", orderId);
        
        Order order = orderService.getOrderById(orderId);
        
        OrderResponse response = OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .productId(order.getProduct().getId())
                .merchantId(order.getMerchant().getId())
                .sku(order.getSku())
                .quantity(order.getQuantity())
                .unitPrice(order.getUnitPrice())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/order-number/{orderNumber}")
    @Operation(summary = "Get order by order number", description = "Retrieves order details by order number")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByOrderNumber(
            @Parameter(description = "Order number", required = true) @PathVariable String orderNumber) {
        log.info("Fetching order with order number: {}", orderNumber);
        
        Order order = orderService.getOrderByOrderNumber(orderNumber);
        
        OrderResponse response = OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .productId(order.getProduct().getId())
                .merchantId(order.getMerchant().getId())
                .sku(order.getSku())
                .quantity(order.getQuantity())
                .unitPrice(order.getUnitPrice())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

