package com.mamoru.transactionsystem.reconciliation.presentation;

import com.mamoru.transactionsystem.common.dto.ApiResponse;
import com.mamoru.transactionsystem.reconciliation.application.ReconciliationService;
import com.mamoru.transactionsystem.reconciliation.domain.ReconciliationReport;
import com.mamoru.transactionsystem.reconciliation.presentation.dto.ReconciliationReportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reconciliation/merchants/{merchantId}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reconciliation", description = "APIs for merchant account reconciliation")
public class ReconciliationController {
    
    private final ReconciliationService reconciliationService;
    
    @PostMapping("/run")
    @Operation(summary = "Manually trigger reconciliation", 
               description = "Manually triggers reconciliation for yesterday's transactions")
    public ResponseEntity<ApiResponse<ReconciliationReportResponse>> runReconciliation(
            @Parameter(description = "Merchant ID", required = true) @PathVariable Long merchantId) {
        log.info("Manually triggering reconciliation for merchant ID: {}", merchantId);
        
        ReconciliationReport report = reconciliationService.reconcileMerchantForYesterday(merchantId);
        
        ReconciliationReportResponse response = ReconciliationReportResponse.builder()
                .id(report.getId())
                .merchantId(report.getMerchant().getId())
                .merchantName(report.getMerchant().getName())
                .reportDate(report.getReportDate())
                .accountBalance(report.getAccountBalance())
                .calculatedSalesValue(report.getCalculatedSalesValue())
                .discrepancy(report.getDiscrepancy())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("Reconciliation completed", response));
    }
    
    @PostMapping("/run/{reportDate}")
    @Operation(summary = "Manually trigger reconciliation for specific date", 
               description = "Manually triggers reconciliation for a specific date")
    public ResponseEntity<ApiResponse<ReconciliationReportResponse>> runReconciliationForDate(
            @Parameter(description = "Merchant ID", required = true) @PathVariable Long merchantId,
            @Parameter(description = "Report date (yyyy-MM-dd)", required = true) 
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate) {
        log.info("Manually triggering reconciliation for merchant ID: {} on date: {}", merchantId, reportDate);
        
        ReconciliationReport report = reconciliationService.reconcileMerchant(merchantId, reportDate);
        
        ReconciliationReportResponse response = ReconciliationReportResponse.builder()
                .id(report.getId())
                .merchantId(report.getMerchant().getId())
                .merchantName(report.getMerchant().getName())
                .reportDate(report.getReportDate())
                .accountBalance(report.getAccountBalance())
                .calculatedSalesValue(report.getCalculatedSalesValue())
                .discrepancy(report.getDiscrepancy())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("Reconciliation completed", response));
    }
    
    @GetMapping("/reports")
    @Operation(summary = "Get all reconciliation reports", 
               description = "Retrieves all reconciliation reports for a merchant")
    public ResponseEntity<ApiResponse<List<ReconciliationReportResponse>>> getReports(
            @Parameter(description = "Merchant ID", required = true) @PathVariable Long merchantId) {
        log.info("Fetching reconciliation reports for merchant ID: {}", merchantId);
        
        List<ReconciliationReport> reports = reconciliationService.getReconciliationReportsByMerchant(merchantId);
        
        List<ReconciliationReportResponse> responses = reports.stream()
                .map(report -> ReconciliationReportResponse.builder()
                        .id(report.getId())
                        .merchantId(report.getMerchant().getId())
                        .merchantName(report.getMerchant().getName())
                        .reportDate(report.getReportDate())
                        .accountBalance(report.getAccountBalance())
                        .calculatedSalesValue(report.getCalculatedSalesValue())
                        .discrepancy(report.getDiscrepancy())
                        .status(report.getStatus())
                        .createdAt(report.getCreatedAt())
                        .build())
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
    
    @GetMapping("/reports/{reportDate}")
    @Operation(summary = "Get reconciliation report for specific date", 
               description = "Retrieves reconciliation report for a specific date")
    public ResponseEntity<ApiResponse<ReconciliationReportResponse>> getReport(
            @Parameter(description = "Merchant ID", required = true) @PathVariable Long merchantId,
            @Parameter(description = "Report date (yyyy-MM-dd)", required = true) 
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate) {
        log.info("Fetching reconciliation report for merchant ID: {} on date: {}", merchantId, reportDate);
        
        ReconciliationReport report = reconciliationService.getReconciliationReport(merchantId, reportDate);
        
        ReconciliationReportResponse response = ReconciliationReportResponse.builder()
                .id(report.getId())
                .merchantId(report.getMerchant().getId())
                .merchantName(report.getMerchant().getName())
                .reportDate(report.getReportDate())
                .accountBalance(report.getAccountBalance())
                .calculatedSalesValue(report.getCalculatedSalesValue())
                .discrepancy(report.getDiscrepancy())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

