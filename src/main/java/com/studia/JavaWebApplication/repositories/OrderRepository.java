package com.studia.JavaWebApplication.repositories;

import com.studia.JavaWebApplication.model.Order;
import com.studia.JavaWebApplication.model.OrderStatus;
import com.studia.JavaWebApplication.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUser(User user, Pageable pageable);
    Page<Order> findAll(Pageable pageable);
    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);
}