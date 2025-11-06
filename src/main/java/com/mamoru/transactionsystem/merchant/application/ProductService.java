package com.mamoru.transactionsystem.merchant.application;

import com.mamoru.transactionsystem.common.exception.ResourceNotFoundException;
import com.mamoru.transactionsystem.merchant.domain.Merchant;
import com.mamoru.transactionsystem.merchant.domain.Product;
import com.mamoru.transactionsystem.merchant.infrastructure.repository.MerchantRepository;
import com.mamoru.transactionsystem.merchant.infrastructure.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final MerchantRepository merchantRepository;
    
    @Transactional
    public Product createProduct(UUID merchantId, Product product) {
        log.info("Creating product for merchant ID: {}, SKU: {}", merchantId, product.getSku());
        
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", merchantId));
        
        if (productRepository.existsByMerchantAndSku(merchant, product.getSku())) {
            throw new IllegalArgumentException(
                String.format("Product with SKU '%s' already exists for this merchant", product.getSku()));
        }
        
        product.setMerchant(merchant);
        Product savedProduct = productRepository.save(product);
        
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return savedProduct;
    }
    
    @Transactional(readOnly = true)
    public Product getProductById(UUID productId) {
        log.debug("Fetching product by ID: {}", productId);
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }
    
    @Transactional(readOnly = true)
    public Product getProductByMerchantIdAndSku(UUID merchantId, String sku) {
        log.debug("Fetching product by merchant ID: {} and SKU: {}", merchantId, sku);
        return productRepository.findByMerchantIdAndSku(merchantId, sku)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Product", "merchantId and sku", merchantId + "/" + sku));
    }
    
    @Transactional(readOnly = true)
    public List<Product> getProductsByMerchantId(UUID merchantId) {
        log.debug("Fetching all products for merchant ID: {}", merchantId);
        return productRepository.findByMerchantId(merchantId);
    }
}

