package com.mamoru.transactionsystem.transaction.infrastructure.repository;

import com.mamoru.transactionsystem.transaction.domain.Order;
import com.mamoru.transactionsystem.transaction.domain.Payment;
import com.mamoru.transactionsystem.transaction.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
    Optional<Payment> findByOrder(Order order);
    
    Optional<Payment> findByOrderId(UUID orderId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByTransactionId(String transactionId);
}

