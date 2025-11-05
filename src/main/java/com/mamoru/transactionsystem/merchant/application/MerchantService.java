package com.mamoru.transactionsystem.merchant.application;

import com.mamoru.transactionsystem.common.exception.ResourceNotFoundException;
import com.mamoru.transactionsystem.merchant.domain.Merchant;
import com.mamoru.transactionsystem.merchant.infrastructure.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class MerchantService {
    
    private final MerchantRepository merchantRepository;
    
    @Transactional
    public Merchant createMerchant(Merchant merchant) {
        log.info("Creating merchant: {}", merchant.getName());
        Merchant savedMerchant = merchantRepository.save(merchant);
        log.info("Merchant created successfully with ID: {}", savedMerchant.getId());
        return savedMerchant;
    }
    
    @Transactional(readOnly = true)
    public Merchant getMerchantById(Long merchantId) {
        log.debug("Fetching merchant by ID: {}", merchantId);
        return merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", merchantId));
    }
    
    @Transactional
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public Merchant creditMerchantAccount(Long merchantId, BigDecimal amount) {
        log.info("Crediting merchant account for merchant ID: {} with amount: {}", merchantId, amount);
        
        Merchant merchant = merchantRepository.findByIdWithLock(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", merchantId));
        
        merchant.credit(amount);
        merchant = merchantRepository.save(merchant);
        
        log.info("Merchant account credited successfully. New balance: {}", merchant.getAccountBalance());
        return merchant;
    }
}

