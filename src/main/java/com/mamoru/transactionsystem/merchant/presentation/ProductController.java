package com.mamoru.transactionsystem.merchant.presentation;

import com.mamoru.transactionsystem.common.dto.ApiResponse;
import com.mamoru.transactionsystem.merchant.application.ProductService;
import com.mamoru.transactionsystem.merchant.domain.Product;
import com.mamoru.transactionsystem.merchant.presentation.dto.ProductRequest;
import com.mamoru.transactionsystem.merchant.presentation.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchants/{merchantId}/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "APIs for product management")
public class ProductController {
    
    private final ProductService productService;
    
    @PostMapping
    @Operation(summary = "Create a new product", description = "Creates a new product for the merchant")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Parameter(description = "Merchant ID", required = true) @PathVariable UUID merchantId,
            @Valid @RequestBody ProductRequest request) {
        log.info("Creating product for merchant ID: {}, SKU: {}", merchantId, request.getSku());
        
        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .price(request.getPrice())
                .currency("USD")
                .build();
        
        Product createdProduct = productService.createProduct(merchantId, product);
        
        ProductResponse response = ProductResponse.builder()
                .id(createdProduct.getId())
                .merchantId(createdProduct.getMerchant().getId())
                .sku(createdProduct.getSku())
                .name(createdProduct.getName())
                .price(createdProduct.getPrice())
                .currency(createdProduct.getCurrency())
                .createdAt(createdProduct.getCreatedAt())
                .updatedAt(createdProduct.getUpdatedAt())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", response));
    }
    
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves all products for a merchant")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts(
            @Parameter(description = "Merchant ID", required = true) @PathVariable UUID merchantId) {
        log.info("Fetching products for merchant ID: {}", merchantId);
        
        List<Product> products = productService.getProductsByMerchantId(merchantId);
        
        List<ProductResponse> responses = products.stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .merchantId(product.getMerchant().getId())
                        .sku(product.getSku())
                        .name(product.getName())
                        .price(product.getPrice())
                        .currency(product.getCurrency())
                        .createdAt(product.getCreatedAt())
                        .updatedAt(product.getUpdatedAt())
                        .build())
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
    
    @GetMapping("/{productId}")
    @Operation(summary = "Get product by ID", description = "Retrieves product details by product ID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @Parameter(description = "Merchant ID", required = true) @PathVariable UUID merchantId,
            @Parameter(description = "Product ID", required = true) @PathVariable UUID productId) {
        log.info("Fetching product ID: {} for merchant ID: {}", productId, merchantId);
        
        Product product = productService.getProductById(productId);
        
        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .merchantId(product.getMerchant().getId())
                .sku(product.getSku())
                .name(product.getName())
                .price(product.getPrice())
                .currency(product.getCurrency())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

