package com.mamoru.transactionsystem.merchant.presentation;

import com.mamoru.transactionsystem.common.dto.ApiResponse;
import com.mamoru.transactionsystem.merchant.application.MerchantService;
import com.mamoru.transactionsystem.merchant.domain.Merchant;
import com.mamoru.transactionsystem.merchant.presentation.dto.MerchantRequest;
import com.mamoru.transactionsystem.merchant.presentation.dto.MerchantResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/merchants")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Merchant Management", description = "APIs for merchant management")
public class MerchantController {
    
    private final MerchantService merchantService;
    
    @PostMapping
    @Operation(summary = "Create a new merchant", description = "Creates a new merchant with an account balance")
    public ResponseEntity<ApiResponse<MerchantResponse>> createMerchant(@Valid @RequestBody MerchantRequest request) {
        log.info("Creating merchant: {}", request.getName());
        
        Merchant merchant = Merchant.builder()
                .name(request.getName())
                .accountBalance(java.math.BigDecimal.ZERO)
                .currency("USD")
                .build();
        
        Merchant createdMerchant = merchantService.createMerchant(merchant);
        
        MerchantResponse response = MerchantResponse.builder()
                .id(createdMerchant.getId())
                .name(createdMerchant.getName())
                .accountBalance(createdMerchant.getAccountBalance())
                .currency(createdMerchant.getCurrency())
                .createdAt(createdMerchant.getCreatedAt())
                .updatedAt(createdMerchant.getUpdatedAt())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Merchant created successfully", response));
    }
    
    @GetMapping("/{merchantId}")
    @Operation(summary = "Get merchant by ID", description = "Retrieves merchant details including account balance")
    public ResponseEntity<ApiResponse<MerchantResponse>> getMerchant(
            @Parameter(description = "Merchant ID", required = true) @PathVariable Long merchantId) {
        log.info("Fetching merchant with ID: {}", merchantId);
        
        Merchant merchant = merchantService.getMerchantById(merchantId);
        
        MerchantResponse response = MerchantResponse.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .accountBalance(merchant.getAccountBalance())
                .currency(merchant.getCurrency())
                .createdAt(merchant.getCreatedAt())
                .updatedAt(merchant.getUpdatedAt())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

