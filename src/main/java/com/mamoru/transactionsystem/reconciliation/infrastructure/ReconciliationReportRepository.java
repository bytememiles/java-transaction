package com.mamoru.transactionsystem.reconciliation.infrastructure;

import com.mamoru.transactionsystem.reconciliation.domain.ReconciliationReport;
import com.mamoru.transactionsystem.merchant.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReconciliationReportRepository extends JpaRepository<ReconciliationReport, Long> {
    
    Optional<ReconciliationReport> findByMerchantAndReportDate(Merchant merchant, LocalDate reportDate);
    
    Optional<ReconciliationReport> findByMerchantIdAndReportDate(Long merchantId, LocalDate reportDate);
    
    List<ReconciliationReport> findByMerchant(Merchant merchant);
    
    List<ReconciliationReport> findByMerchantId(Long merchantId);
    
    List<ReconciliationReport> findByReportDate(LocalDate reportDate);
}

