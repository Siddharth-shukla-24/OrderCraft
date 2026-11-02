package com.ordercraft.controller;

import com.ordercraft.dto.BillResponseDto;
import com.ordercraft.dto.PaymentRequestDto;
import com.ordercraft.dto.PaymentResponseDto;
import com.ordercraft.entity.BillStatus;
import com.ordercraft.service.BillService;
import com.ordercraft.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;
    private final PaymentService paymentService;

    public BillController(BillService billService, PaymentService paymentService) {
        this.billService = billService;
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<BillResponseDto>> getAllBills(
            @RequestParam(required = false) BillStatus status,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(billService.getAllBills(status, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillResponseDto> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(billService.getBillById(id));
    }

    @PostMapping("/{id}/issue")
    public ResponseEntity<BillResponseDto> issueBill(@PathVariable Long id) {
        return ResponseEntity.ok(billService.issueBill(id));
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<PaymentResponseDto> recordPayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequestDto request) {
        PaymentResponseDto payment = paymentService.recordPayment(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<List<PaymentResponseDto>> getPayments(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentsByBillId(id));
    }
}
