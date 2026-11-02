package com.ordercraft.repository;

import com.ordercraft.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findAllByOrderBySupplierNameAsc();
    List<Supplier> findByStatusOrderBySupplierNameAsc(String status);
    long countByStatus(String status);
}
