package com.mamoru.transactionsystem.merchant.infrastructure.repository;

import com.mamoru.transactionsystem.merchant.domain.Inventory;
import com.mamoru.transactionsystem.merchant.domain.InventoryTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, UUID> {
    
    List<InventoryTransaction> findByInventory(Inventory inventory);
    
    Page<InventoryTransaction> findByInventory(Inventory inventory, Pageable pageable);
}

