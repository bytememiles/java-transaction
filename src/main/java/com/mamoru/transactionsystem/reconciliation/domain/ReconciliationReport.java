package com.mamoru.transactionsystem.reconciliation.domain;

import com.mamoru.transactionsystem.merchant.domain.Merchant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reconciliation_reports", uniqueConstraints = {
    @UniqueConstraint(name = "uk_reconciliation_merchant_date", 
        columnNames = {"merchant_id", "report_date"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReconciliationReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;
    
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;
    
    @Column(name = "account_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal accountBalance;
    
    @Column(name = "calculated_sales_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal calculatedSalesValue;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal discrepancy;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReconciliationStatus status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    /**
     * Calculates the discrepancy between account balance and calculated sales value.
     * @return the discrepancy amount (positive means account has more, negative means less)
     */
    public BigDecimal calculateDiscrepancy() {
        return accountBalance.subtract(calculatedSalesValue);
    }
    
    /**
     * Determines if there is a discrepancy.
     * @return true if discrepancy is not zero
     */
    public boolean hasDiscrepancy() {
        return discrepancy.compareTo(BigDecimal.ZERO) != 0;
    }
}

