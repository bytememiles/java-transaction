package com.mamoru.transactionsystem.user.infrastructure.repository;

import com.mamoru.transactionsystem.user.domain.Account;
import com.mamoru.transactionsystem.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    
    Optional<Account> findByUser(User user);
    
    Optional<Account> findByUserId(UUID userId);
    
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdWithLock(@Param("id") UUID id);
    
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId")
    Optional<Account> findByUserIdWithLock(@Param("userId") UUID userId);
}

