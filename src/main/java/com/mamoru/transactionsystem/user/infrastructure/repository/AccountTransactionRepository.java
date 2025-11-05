package com.mamoru.transactionsystem.user.infrastructure.repository;

import com.mamoru.transactionsystem.user.domain.Account;
import com.mamoru.transactionsystem.user.domain.AccountTransaction;
import com.mamoru.transactionsystem.user.domain.AccountTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {
    
    List<AccountTransaction> findByAccount(Account account);
    
    Page<AccountTransaction> findByAccount(Account account, Pageable pageable);
    
    List<AccountTransaction> findByAccountAndTransactionType(Account account, AccountTransactionType type);
    
    @Query("SELECT at FROM AccountTransaction at WHERE at.account.id = :accountId " +
           "AND at.createdAt BETWEEN :startDate AND :endDate ORDER BY at.createdAt DESC")
    List<AccountTransaction> findByAccountIdAndDateRange(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}

