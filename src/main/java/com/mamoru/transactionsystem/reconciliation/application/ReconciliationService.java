package com.mamoru.transactionsystem.reconciliation.application;

import com.mamoru.transactionsystem.common.exception.ResourceNotFoundException;
import com.mamoru.transactionsystem.merchant.application.MerchantService;
import com.mamoru.transactionsystem.merchant.domain.Merchant;
import com.mamoru.transactionsystem.merchant.infrastructure.repository.MerchantRepository;
import com.mamoru.transactionsystem.reconciliation.domain.ReconciliationReport;
import com.mamoru.transactionsystem.reconciliation.domain.ReconciliationStatus;
import com.mamoru.transactionsystem.reconciliation.infrastructure.ReconciliationReportRepository;
import com.mamoru.transactionsystem.transaction.infrastructure.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReconciliationService {
    
    private final ReconciliationReportRepository reconciliationReportRepository;
    private final MerchantService merchantService;
    private final MerchantRepository merchantRepository;
    private final OrderRepository orderRepository;
    
    @Transactional
    public ReconciliationReport reconcileMerchant(UUID merchantId, LocalDate reportDate) {
        log.info("Starting reconciliation for merchant ID: {} on date: {}", merchantId, reportDate);
        
        Merchant merchant = merchantService.getMerchantById(merchantId);
        
        // Check if report already exists for this date
        ReconciliationReport existingReport = reconciliationReportRepository
                .findByMerchantIdAndReportDate(merchantId, reportDate)
                .orElse(null);
        
        if (existingReport != null) {
            log.warn("Reconciliation report already exists for merchant ID: {} on date: {}", merchantId, reportDate);
            return existingReport;
        }
        
        // Get account balance
        BigDecimal accountBalance = merchant.getAccountBalance();
        
        // Calculate date range for the report date (full day)
        LocalDateTime startDate = reportDate.atStartOfDay();
        LocalDateTime endDate = reportDate.atTime(LocalTime.MAX);
        
        // Calculate total sales value from completed orders
        BigDecimal calculatedSalesValue = orderRepository.calculateTotalSalesValue(
                merchantId, startDate, endDate);
        
        // Calculate discrepancy
        BigDecimal discrepancy = accountBalance.subtract(calculatedSalesValue);
        
        // Determine status
        ReconciliationStatus status = discrepancy.compareTo(BigDecimal.ZERO) == 0
                ? ReconciliationStatus.MATCHED
                : ReconciliationStatus.DISCREPANCY;
        
        // Create reconciliation report
        ReconciliationReport report = ReconciliationReport.builder()
                .merchant(merchant)
                .reportDate(reportDate)
                .accountBalance(accountBalance)
                .calculatedSalesValue(calculatedSalesValue)
                .discrepancy(discrepancy)
                .status(status)
                .build();
        
        report = reconciliationReportRepository.save(report);
        
        log.info("Reconciliation completed for merchant ID: {}. Status: {}, Discrepancy: {}", 
                merchantId, status, discrepancy);
        
        if (status == ReconciliationStatus.DISCREPANCY) {
            log.warn("DISCREPANCY DETECTED for merchant ID: {}. Account Balance: {}, Calculated Sales: {}, Discrepancy: {}", 
                    merchantId, accountBalance, calculatedSalesValue, discrepancy);
        }
        
        return report;
    }
    
    @Transactional
    public ReconciliationReport reconcileMerchantForYesterday(UUID merchantId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return reconcileMerchant(merchantId, yesterday);
    }
    
    @Transactional
    public void reconcileAllMerchantsForYesterday() {
        log.info("Starting reconciliation for all merchants for yesterday");
        
        // Get all merchants
        List<Merchant> allMerchants = merchantRepository.findAll();
        
        if (allMerchants.isEmpty()) {
            log.info("No merchants found to reconcile");
            return;
        }
        
        log.info("Found {} merchants to reconcile", allMerchants.size());
        
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        for (Merchant merchant : allMerchants) {
            try {
                reconcileMerchant(merchant.getId(), yesterday);
            } catch (Exception e) {
                log.error("Error reconciling merchant ID: {}", merchant.getId(), e);
            }
        }
        
        log.info("Completed reconciliation for all merchants");
    }
    
    @Transactional(readOnly = true)
    public ReconciliationReport getReconciliationReport(UUID merchantId, LocalDate reportDate) {
        return reconciliationReportRepository
                .findByMerchantIdAndReportDate(merchantId, reportDate)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ReconciliationReport", "merchantId and reportDate", merchantId + "/" + reportDate));
    }
    
    @Transactional(readOnly = true)
    public List<ReconciliationReport> getReconciliationReportsByMerchant(UUID merchantId) {
        return reconciliationReportRepository.findByMerchantId(merchantId);
    }
}

