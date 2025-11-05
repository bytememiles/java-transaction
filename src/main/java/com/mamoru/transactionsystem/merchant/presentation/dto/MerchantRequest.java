package com.mamoru.transactionsystem.merchant.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantRequest {
    
    @NotBlank(message = "Merchant name is required")
    @Size(min = 1, max = 255, message = "Merchant name must be between 1 and 255 characters")
    private String name;
}

