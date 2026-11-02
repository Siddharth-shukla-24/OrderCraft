package com.ordercraft.controller;

import com.ordercraft.dto.SupplierRequestDto;
import com.ordercraft.dto.SupplierResponseDto;
import com.ordercraft.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponseDto>> getAllSuppliers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(supplierService.getAllSuppliers(status, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @PostMapping
    public ResponseEntity<SupplierResponseDto> createSupplier(@Valid @RequestBody SupplierRequestDto request) {
        SupplierResponseDto created = supplierService.createSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequestDto request) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SupplierResponseDto> updateSupplierStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(supplierService.updateSupplierStatus(id, status));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SupplierResponseDto> putSupplierStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(supplierService.updateSupplierStatus(id, status));
    }
}
