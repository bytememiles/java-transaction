package com.mamoru.transactionsystem.transaction.presentation.dto;

import com.mamoru.transactionsystem.transaction.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private String orderNumber;
    private UUID userId;
    private UUID productId;
    private UUID merchantId;
    private String sku;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

