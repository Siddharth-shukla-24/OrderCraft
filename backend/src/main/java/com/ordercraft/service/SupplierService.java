package com.ordercraft.service;

import com.ordercraft.dto.SupplierRequestDto;
import com.ordercraft.dto.SupplierResponseDto;
import com.ordercraft.entity.Supplier;
import com.ordercraft.exception.BusinessRuleException;
import com.ordercraft.exception.ResourceNotFoundException;
import com.ordercraft.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional(readOnly = true)
    public List<SupplierResponseDto> getAllSuppliers(String status, String search) {
        List<Supplier> list = (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status))
                ? supplierRepository.findByStatusOrderBySupplierNameAsc(status.toUpperCase())
                : supplierRepository.findAllByOrderBySupplierNameAsc();

        return list.stream()
                .filter(s -> {
                    if (search == null || search.isBlank()) return true;
                    String q = search.trim().toLowerCase();
                    return s.getSupplierName().toLowerCase().contains(q)
                            || s.getContactPerson().toLowerCase().contains(q)
                            || s.getEmail().toLowerCase().contains(q)
                            || s.getPhone().toLowerCase().contains(q)
                            || (s.getAddress() != null && s.getAddress().toLowerCase().contains(q));
                })
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public SupplierResponseDto getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        return toDto(supplier);
    }

    @Transactional
    public SupplierResponseDto createSupplier(SupplierRequestDto request) {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(request.getSupplierName().trim());
        supplier.setContactPerson(request.getContactPerson().trim());
        supplier.setEmail(request.getEmail().trim());
        supplier.setPhone(request.getPhone().trim());
        supplier.setAddress(request.getAddress() != null ? request.getAddress().trim() : "");
        supplier.setStatus(request.getStatus() != null && !request.getStatus().isBlank()
                ? request.getStatus().toUpperCase()
                : "ACTIVE");

        Supplier saved = supplierRepository.save(supplier);
        return toDto(saved);
    }

    @Transactional
    public SupplierResponseDto updateSupplier(Long id, SupplierRequestDto request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        supplier.setSupplierName(request.getSupplierName().trim());
        supplier.setContactPerson(request.getContactPerson().trim());
        supplier.setEmail(request.getEmail().trim());
        supplier.setPhone(request.getPhone().trim());
        supplier.setAddress(request.getAddress() != null ? request.getAddress().trim() : "");

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            validateStatus(request.getStatus());
            supplier.setStatus(request.getStatus().toUpperCase());
        }

        Supplier saved = supplierRepository.save(supplier);
        return toDto(saved);
    }

    @Transactional
    public SupplierResponseDto updateSupplierStatus(Long id, String status) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        validateStatus(status);
        supplier.setStatus(status.toUpperCase());
        Supplier saved = supplierRepository.save(supplier);
        return toDto(saved);
    }

    private void validateStatus(String status) {
        if (!"ACTIVE".equalsIgnoreCase(status) && !"INACTIVE".equalsIgnoreCase(status)) {
            throw new BusinessRuleException("Status must be ACTIVE or INACTIVE");
        }
    }

    public SupplierResponseDto toDto(Supplier s) {
        return new SupplierResponseDto(
                s.getSupplierId(),
                s.getSupplierName(),
                s.getContactPerson(),
                s.getEmail(),
                s.getPhone(),
                s.getAddress(),
                s.getStatus()
        );
    }
}
