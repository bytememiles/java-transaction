package com.mamoru.transactionsystem.merchant.presentation;

import com.mamoru.transactionsystem.common.dto.ApiResponse;
import com.mamoru.transactionsystem.merchant.application.InventoryService;
import com.mamoru.transactionsystem.merchant.domain.Inventory;
import com.mamoru.transactionsystem.merchant.presentation.dto.AddInventoryRequest;
import com.mamoru.transactionsystem.merchant.presentation.dto.InventoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchants/{merchantId}/inventory")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventory Management", description = "APIs for inventory management")
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    @PostMapping("/products/{productId}/add")
    @Operation(summary = "Add inventory quantity", description = "Adds quantity to the inventory for a product")
    public ResponseEntity<ApiResponse<InventoryResponse>> addInventory(
            @Parameter(description = "Merchant ID", required = true) @PathVariable UUID merchantId,
            @Parameter(description = "Product ID", required = true) @PathVariable UUID productId,
            @Valid @RequestBody AddInventoryRequest request) {
        log.info("Adding inventory for product ID: {}, quantity: {}", productId, request.getQuantity());
        
        Inventory inventory = inventoryService.addInventory(productId, request.getQuantity(), null);
        
        InventoryResponse response = InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productSku(inventory.getProduct().getSku())
                .productName(inventory.getProduct().getName())
                .quantity(inventory.getQuantity())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("Inventory added successfully", response));
    }
    
    @GetMapping
    @Operation(summary = "Get all inventories", description = "Retrieves all inventory records for a merchant")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventories(
            @Parameter(description = "Merchant ID", required = true) @PathVariable UUID merchantId) {
        log.info("Fetching inventories for merchant ID: {}", merchantId);
        
        List<Inventory> inventories = inventoryService.getInventoriesByMerchantId(merchantId);
        
        List<InventoryResponse> responses = inventories.stream()
                .map(inventory -> InventoryResponse.builder()
                        .id(inventory.getId())
                        .productId(inventory.getProduct().getId())
                        .productSku(inventory.getProduct().getSku())
                        .productName(inventory.getProduct().getName())
                        .quantity(inventory.getQuantity())
                        .createdAt(inventory.getCreatedAt())
                        .updatedAt(inventory.getUpdatedAt())
                        .build())
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
    
    @GetMapping("/products/{productId}")
    @Operation(summary = "Get inventory by product ID", description = "Retrieves inventory details for a specific product")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventory(
            @Parameter(description = "Merchant ID", required = true) @PathVariable UUID merchantId,
            @Parameter(description = "Product ID", required = true) @PathVariable UUID productId) {
        log.info("Fetching inventory for product ID: {}", productId);
        
        Inventory inventory = inventoryService.getInventoryByProductId(productId);
        
        InventoryResponse response = InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productSku(inventory.getProduct().getSku())
                .productName(inventory.getProduct().getName())
                .quantity(inventory.getQuantity())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

