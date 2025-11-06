package com.mamoru.transactionsystem.merchant.infrastructure.repository;

import com.mamoru.transactionsystem.merchant.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT m FROM Merchant m WHERE m.id = :id")
    Optional<Merchant> findByIdWithLock(@Param("id") UUID id);
    
    List<Merchant> findAll();
}

