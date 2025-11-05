package com.mamoru.transactionsystem.transaction.domain;

import com.mamoru.transactionsystem.merchant.domain.Merchant;
import com.mamoru.transactionsystem.merchant.domain.Product;
import com.mamoru.transactionsystem.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders", uniqueConstraints = {
    @UniqueConstraint(name = "uk_orders_order_number", columnNames = {"order_number"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;
    
    @Column(nullable = false, length = 100)
    private String sku;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;
    
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
     * Marks the order as completed.
     */
    public void markAsCompleted() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException(
                String.format("Cannot complete order. Current status: %s", this.status));
        }
        this.status = OrderStatus.COMPLETED;
    }
    
    /**
     * Marks the order as failed.
     */
    public void markAsFailed() {
        if (this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot fail a completed order");
        }
        this.status = OrderStatus.FAILED;
    }
    
    /**
     * Marks the order as refunded.
     */
    public void markAsRefunded() {
        if (this.status != OrderStatus.COMPLETED) {
            throw new IllegalStateException(
                String.format("Cannot refund order. Current status: %s", this.status));
        }
        this.status = OrderStatus.REFUNDED;
    }
    
    /**
     * Checks if the order is in a terminal state (cannot be modified).
     * @return true if order is completed, failed, or refunded
     */
    public boolean isInTerminalState() {
        return status == OrderStatus.COMPLETED 
            || status == OrderStatus.FAILED 
            || status == OrderStatus.REFUNDED;
    }
}

