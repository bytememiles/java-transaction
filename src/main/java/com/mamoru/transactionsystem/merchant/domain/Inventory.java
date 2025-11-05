package com.mamoru.transactionsystem.merchant.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory", uniqueConstraints = {
    @UniqueConstraint(name = "uk_inventory_product_id", columnNames = {"product_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;
    
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
     * Adds quantity to inventory.
     * @param quantityToAdd the quantity to add (must be positive)
     * @throws IllegalArgumentException if quantity is not positive
     */
    public void addQuantity(Integer quantityToAdd) {
        if (quantityToAdd == null || quantityToAdd <= 0) {
            throw new IllegalArgumentException("Quantity to add must be positive");
        }
        this.quantity = this.quantity + quantityToAdd;
    }
    
    /**
     * Deducts quantity from inventory.
     * @param quantityToDeduct the quantity to deduct (must be positive)
     * @throws IllegalArgumentException if quantity is not positive or insufficient stock
     */
    public void deductQuantity(Integer quantityToDeduct) {
        if (quantityToDeduct == null || quantityToDeduct <= 0) {
            throw new IllegalArgumentException("Quantity to deduct must be positive");
        }
        if (this.quantity < quantityToDeduct) {
            throw new IllegalArgumentException(
                String.format("Insufficient stock. Available: %d, Requested: %d", 
                    this.quantity, quantityToDeduct));
        }
        this.quantity = this.quantity - quantityToDeduct;
    }
    
    /**
     * Checks if there is sufficient stock available.
     * @param requestedQuantity the quantity to check
     * @return true if sufficient stock is available
     */
    public boolean hasSufficientStock(Integer requestedQuantity) {
        return requestedQuantity != null && this.quantity >= requestedQuantity;
    }
}

