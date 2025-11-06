package com.mamoru.transactionsystem.merchant.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private UUID id;
    private UUID productId;
    private String productSku;
    private String productName;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

