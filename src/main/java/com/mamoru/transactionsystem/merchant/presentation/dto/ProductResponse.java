package com.mamoru.transactionsystem.merchant.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private Long merchantId;
    private String sku;
    private String name;
    private BigDecimal price;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

