package com.mamoru.transactionsystem.merchant.application;

import com.mamoru.transactionsystem.common.exception.ResourceNotFoundException;
import com.mamoru.transactionsystem.merchant.domain.Inventory;
import com.mamoru.transactionsystem.merchant.domain.Product;
import com.mamoru.transactionsystem.merchant.infrastructure.repository.InventoryRepository;
import com.mamoru.transactionsystem.merchant.infrastructure.repository.InventoryTransactionRepository;
import com.mamoru.transactionsystem.merchant.infrastructure.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
    
    @Mock
    private InventoryRepository inventoryRepository;
    
    @Mock
    private InventoryTransactionRepository inventoryTransactionRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private InventoryService inventoryService;
    
    private static final UUID PRODUCT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID INVENTORY_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    
    private Product product;
    private Inventory inventory;
    
    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(PRODUCT_ID)
                .sku("TEST-001")
                .name("Test Product")
                .build();
        
        inventory = Inventory.builder()
                .id(INVENTORY_ID)
                .product(product)
                .quantity(100)
                .build();
    }
    
    @Test
    void testAddInventory_Success() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductIdWithLock(PRODUCT_ID)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
        when(inventoryTransactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        Inventory result = inventoryService.addInventory(PRODUCT_ID, 50, "REF-001");
        
        assertNotNull(result);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
        verify(inventoryTransactionRepository, times(1)).save(any());
    }
    
    @Test
    void testAddInventory_ProductNotFound() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, 
                () -> inventoryService.addInventory(PRODUCT_ID, 50, "REF-001"));
    }
    
    @Test
    void testDeductInventory_Success() {
        when(inventoryRepository.findByProductIdWithLock(PRODUCT_ID)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
        when(inventoryTransactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        Inventory result = inventoryService.deductInventory(PRODUCT_ID, 30, "REF-001");
        
        assertNotNull(result);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
        verify(inventoryTransactionRepository, times(1)).save(any());
    }
    
    @Test
    void testDeductInventory_NotFound() {
        when(inventoryRepository.findByProductIdWithLock(PRODUCT_ID)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, 
                () -> inventoryService.deductInventory(PRODUCT_ID, 30, "REF-001"));
    }
}

