package com.ordercraft.repository;

import com.ordercraft.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByOrderOrderId(Long orderId);
    Optional<Bill> findByBillNumber(String billNumber);
    boolean existsByOrderOrderId(Long orderId);
    boolean existsByBillNumber(String billNumber);
    List<Bill> findAllByOrderByBillDateDescBillIdDesc();
}
