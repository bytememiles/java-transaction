package com.mamoru.transactionsystem.user.application;

import com.mamoru.transactionsystem.common.exception.InsufficientBalanceException;
import com.mamoru.transactionsystem.common.exception.ResourceNotFoundException;
import com.mamoru.transactionsystem.payment.gateway.application.PaymentGatewayService;
import com.mamoru.transactionsystem.user.domain.Account;
import com.mamoru.transactionsystem.user.domain.AccountTransaction;
import com.mamoru.transactionsystem.user.domain.AccountTransactionType;
import com.mamoru.transactionsystem.user.infrastructure.repository.AccountRepository;
import com.mamoru.transactionsystem.user.infrastructure.repository.AccountTransactionRepository;
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
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;
    private final PaymentGatewayService paymentGatewayService;
    
    @Transactional(readOnly = true)
    public Account getAccountByUserId(Long userId) {
        log.debug("Fetching account for user ID: {}", userId);
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "userId", userId));
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getBalanceByUserId(Long userId) {
        Account account = getAccountByUserId(userId);
        return account.getBalance();
    }
    
    @Transactional
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public Account rechargeAccount(Long userId, BigDecimal amount) {
        log.info("Recharging account for user ID: {} with amount: {}", userId, amount);
        
        Account account = accountRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "userId", userId));
        
        BigDecimal balanceBefore = account.getBalance();
        
        // Call payment gateway to process recharge
        String transactionId = paymentGatewayService.processRecharge(userId, amount);
        
        // Credit the account
        account.credit(amount);
        account = accountRepository.save(account);
        
        BigDecimal balanceAfter = account.getBalance();
        
        // Create audit trail
        AccountTransaction transaction = AccountTransaction.builder()
                .account(account)
                .transactionType(AccountTransactionType.RECHARGE)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .referenceId(transactionId)
                .build();
        accountTransactionRepository.save(transaction);
        
        log.info("Account recharged successfully. Balance before: {}, after: {}", balanceBefore, balanceAfter);
        return account;
    }
    
    @Transactional
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public Account debitAccount(Long userId, BigDecimal amount, String referenceId) {
        log.info("Debiting account for user ID: {} with amount: {}", userId, amount);
        
        Account account = accountRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "userId", userId));
        
        BigDecimal balanceBefore = account.getBalance();
        
        // Check and debit
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(account.getBalance(), amount);
        }
        
        account.debit(amount);
        account = accountRepository.save(account);
        
        BigDecimal balanceAfter = account.getBalance();
        
        // Create audit trail
        AccountTransaction transaction = AccountTransaction.builder()
                .account(account)
                .transactionType(AccountTransactionType.DEBIT)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .referenceId(referenceId)
                .build();
        accountTransactionRepository.save(transaction);
        
        log.info("Account debited successfully. Balance before: {}, after: {}", balanceBefore, balanceAfter);
        return account;
    }
    
    @Transactional
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public Account creditAccount(Long userId, BigDecimal amount, String referenceId) {
        log.info("Crediting account for user ID: {} with amount: {}", userId, amount);
        
        Account account = accountRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "userId", userId));
        
        BigDecimal balanceBefore = account.getBalance();
        
        account.credit(amount);
        account = accountRepository.save(account);
        
        BigDecimal balanceAfter = account.getBalance();
        
        // Create audit trail
        AccountTransaction transaction = AccountTransaction.builder()
                .account(account)
                .transactionType(AccountTransactionType.CREDIT)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .referenceId(referenceId)
                .build();
        accountTransactionRepository.save(transaction);
        
        log.info("Account credited successfully. Balance before: {}, after: {}", balanceBefore, balanceAfter);
        return account;
    }
}

