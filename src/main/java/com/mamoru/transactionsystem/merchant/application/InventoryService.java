package com.mamoru.transactionsystem.merchant.application;

import com.mamoru.transactionsystem.common.exception.ResourceNotFoundException;
import com.mamoru.transactionsystem.merchant.domain.Inventory;
import com.mamoru.transactionsystem.merchant.domain.InventoryTransaction;
import com.mamoru.transactionsystem.merchant.domain.InventoryTransactionType;
import com.mamoru.transactionsystem.merchant.domain.Product;
import com.mamoru.transactionsystem.merchant.infrastructure.repository.InventoryRepository;
import com.mamoru.transactionsystem.merchant.infrastructure.repository.InventoryTransactionRepository;
import com.mamoru.transactionsystem.merchant.infrastructure.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ProductRepository productRepository;
    
    @Transactional(readOnly = true)
    public Inventory getInventoryByProductId(Long productId) {
        log.debug("Fetching inventory for product ID: {}", productId);
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
    }
    
    @Transactional(readOnly = true)
    public List<Inventory> getInventoriesByMerchantId(Long merchantId) {
        log.debug("Fetching all inventories for merchant ID: {}", merchantId);
        List<Product> products = productRepository.findByMerchantId(merchantId);
        return products.stream()
                .map(product -> inventoryRepository.findByProduct(product).orElse(null))
                .filter(inventory -> inventory != null)
                .toList();
    }
    
    @Transactional
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public Inventory addInventory(Long productId, Integer quantity, String referenceId) {
        log.info("Adding inventory for product ID: {}, quantity: {}", productId, quantity);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseGet(() -> {
                    Inventory newInventory = Inventory.builder()
                            .product(product)
                            .quantity(0)
                            .build();
                    return inventoryRepository.save(newInventory);
                });
        
        Integer quantityBefore = inventory.getQuantity();
        
        inventory.addQuantity(quantity);
        inventory = inventoryRepository.save(inventory);
        
        Integer quantityAfter = inventory.getQuantity();
        
        // Create audit trail
        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventory(inventory)
                .transactionType(InventoryTransactionType.ADD)
                .quantity(quantity)
                .quantityBefore(quantityBefore)
                .quantityAfter(quantityAfter)
                .referenceId(referenceId)
                .build();
        inventoryTransactionRepository.save(transaction);
        
        log.info("Inventory added successfully. Quantity before: {}, after: {}", quantityBefore, quantityAfter);
        return inventory;
    }
    
    @Transactional
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public Inventory deductInventory(Long productId, Integer quantity, String referenceId) {
        log.info("Deducting inventory for product ID: {}, quantity: {}", productId, quantity);
        
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
        
        Integer quantityBefore = inventory.getQuantity();
        
        inventory.deductQuantity(quantity);
        inventory = inventoryRepository.save(inventory);
        
        Integer quantityAfter = inventory.getQuantity();
        
        // Create audit trail
        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventory(inventory)
                .transactionType(InventoryTransactionType.DEDUCT)
                .quantity(quantity)
                .quantityBefore(quantityBefore)
                .quantityAfter(quantityAfter)
                .referenceId(referenceId)
                .build();
        inventoryTransactionRepository.save(transaction);
        
        log.info("Inventory deducted successfully. Quantity before: {}, after: {}", quantityBefore, quantityAfter);
        return inventory;
    }
}

