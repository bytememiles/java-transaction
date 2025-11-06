package com.mamoru.transactionsystem.reconciliation.presentation.dto;

import com.mamoru.transactionsystem.reconciliation.domain.ReconciliationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationReportResponse {
    private UUID id;
    private UUID merchantId;
    private String merchantName;
    private LocalDate reportDate;
    private BigDecimal accountBalance;
    private BigDecimal calculatedSalesValue;
    private BigDecimal discrepancy;
    private ReconciliationStatus status;
    private LocalDateTime createdAt;
}

