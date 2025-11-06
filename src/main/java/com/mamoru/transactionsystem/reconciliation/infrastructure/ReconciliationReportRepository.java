package com.mamoru.transactionsystem.reconciliation.infrastructure;

import com.mamoru.transactionsystem.reconciliation.domain.ReconciliationReport;
import com.mamoru.transactionsystem.merchant.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReconciliationReportRepository extends JpaRepository<ReconciliationReport, UUID> {
    
    Optional<ReconciliationReport> findByMerchantAndReportDate(Merchant merchant, LocalDate reportDate);
    
    Optional<ReconciliationReport> findByMerchantIdAndReportDate(UUID merchantId, LocalDate reportDate);
    
    List<ReconciliationReport> findByMerchant(Merchant merchant);
    
    List<ReconciliationReport> findByMerchantId(UUID merchantId);
    
    List<ReconciliationReport> findByReportDate(LocalDate reportDate);
}

