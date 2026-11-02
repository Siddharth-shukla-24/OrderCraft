package com.ordercraft.repository;

import com.ordercraft.entity.CustomerOrder;
import com.ordercraft.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findAllByOrderByOrderDateDescOrderIdDesc();
    List<CustomerOrder> findByStatus(OrderStatus status);
    List<CustomerOrder> findByCustomerCustomerId(Long customerId);
    long countByStatus(OrderStatus status);
    long countByStatusNot(OrderStatus status);
}
