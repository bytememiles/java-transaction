package com.mamoru.transactionsystem.merchant.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "merchants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Merchant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(name = "account_balance", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal accountBalance = BigDecimal.ZERO;
    
    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";
    
    @Version
    private Long version;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Credits the merchant account with the specified amount.
     * @param amount the amount to credit (must be positive)
     * @throws IllegalArgumentException if amount is not positive
     */
    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        this.accountBalance = this.accountBalance.add(amount);
    }
    
    /**
     * Debits the merchant account with the specified amount.
     * @param amount the amount to debit (must be positive)
     * @throws IllegalArgumentException if amount is not positive or insufficient balance
     */
    public void debit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        if (accountBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient merchant account balance");
        }
        this.accountBalance = this.accountBalance.subtract(amount);
    }
}

