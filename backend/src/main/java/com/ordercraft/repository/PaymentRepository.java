package com.ordercraft.repository;

import com.ordercraft.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBillBillId(Long billId);
    List<Payment> findByBillBillIdOrderByPaymentDateDescPaymentIdDesc(Long billId);
}
