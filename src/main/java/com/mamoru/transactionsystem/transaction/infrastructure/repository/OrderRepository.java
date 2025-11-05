package com.mamoru.transactionsystem.transaction.infrastructure.repository;

import com.mamoru.transactionsystem.transaction.domain.Order;
import com.mamoru.transactionsystem.transaction.domain.OrderStatus;
import com.mamoru.transactionsystem.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByUser(User user);
    
    Page<Order> findByUser(User user, Pageable pageable);
    
    List<Order> findByMerchantId(Long merchantId);
    
    Page<Order> findByMerchantId(Long merchantId, Pageable pageable);
    
    List<Order> findByStatus(OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.merchant.id = :merchantId " +
           "AND o.status = 'COMPLETED' " +
           "AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findCompletedOrdersByMerchantAndDateRange(
            @Param("merchantId") Long merchantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
           "WHERE o.merchant.id = :merchantId " +
           "AND o.status = 'COMPLETED' " +
           "AND o.createdAt BETWEEN :startDate AND :endDate")
    java.math.BigDecimal calculateTotalSalesValue(
            @Param("merchantId") Long merchantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}

