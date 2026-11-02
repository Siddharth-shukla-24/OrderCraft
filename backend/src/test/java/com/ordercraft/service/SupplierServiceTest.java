package com.ordercraft.service;

import com.ordercraft.dto.SupplierRequestDto;
import com.ordercraft.dto.SupplierResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SupplierServiceTest {

    @Autowired
    private SupplierService supplierService;

    @Test
    void testSupplierCrudAndStatusToggle() {
        SupplierRequestDto createDto = new SupplierRequestDto(
                "Apex Raw Materials",
                "Arun Sharma",
                "arun@apexraw.example.com",
                "+91 99999 11111",
                "Phase 1, Industrial Area",
                "ACTIVE"
        );

        SupplierResponseDto created = supplierService.createSupplier(createDto);
        assertNotNull(created.getSupplierId());
        assertEquals("Apex Raw Materials", created.getSupplierName());
        assertEquals("ACTIVE", created.getStatus());

        // Update
        SupplierRequestDto updateDto = new SupplierRequestDto(
                "Apex Raw Materials Pvt Ltd",
                "Arun Sharma",
                "arun@apexraw.example.com",
                "+91 99999 22222",
                "Phase 1, Industrial Area",
                "ACTIVE"
        );
        SupplierResponseDto updated = supplierService.updateSupplier(created.getSupplierId(), updateDto);
        assertEquals("Apex Raw Materials Pvt Ltd", updated.getSupplierName());
        assertEquals("+91 99999 22222", updated.getPhone());

        // Deactivate status
        SupplierResponseDto inactive = supplierService.updateSupplierStatus(created.getSupplierId(), "INACTIVE");
        assertEquals("INACTIVE", inactive.getStatus());

        // Search
        List<SupplierResponseDto> found = supplierService.getAllSuppliers("ALL", "Apex Raw");
        assertFalse(found.isEmpty());
        assertEquals("Apex Raw Materials Pvt Ltd", found.get(0).getSupplierName());
    }
}
