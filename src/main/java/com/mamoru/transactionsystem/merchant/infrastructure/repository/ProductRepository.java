package com.mamoru.transactionsystem.merchant.infrastructure.repository;

import com.mamoru.transactionsystem.merchant.domain.Merchant;
import com.mamoru.transactionsystem.merchant.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    
    List<Product> findByMerchant(Merchant merchant);
    
    List<Product> findByMerchantId(UUID merchantId);
    
    Optional<Product> findByMerchantAndSku(Merchant merchant, String sku);
    
    Optional<Product> findByMerchantIdAndSku(UUID merchantId, String sku);
    
    boolean existsByMerchantAndSku(Merchant merchant, String sku);
    
    boolean existsByMerchantIdAndSku(UUID merchantId, String sku);
}

