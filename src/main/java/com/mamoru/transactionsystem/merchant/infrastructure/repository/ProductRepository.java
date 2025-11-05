package com.mamoru.transactionsystem.merchant.infrastructure.repository;

import com.mamoru.transactionsystem.merchant.domain.Merchant;
import com.mamoru.transactionsystem.merchant.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByMerchant(Merchant merchant);
    
    List<Product> findByMerchantId(Long merchantId);
    
    Optional<Product> findByMerchantAndSku(Merchant merchant, String sku);
    
    Optional<Product> findByMerchantIdAndSku(Long merchantId, String sku);
    
    boolean existsByMerchantAndSku(Merchant merchant, String sku);
    
    boolean existsByMerchantIdAndSku(Long merchantId, String sku);
}

