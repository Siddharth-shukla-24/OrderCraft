package com.ordercraft.controller;

import com.ordercraft.dto.BillResponseDto;
import com.ordercraft.dto.OrderCreateRequestDto;
import com.ordercraft.dto.OrderResponseDto;
import com.ordercraft.dto.OrderUpdateRequestDto;
import com.ordercraft.entity.OrderStatus;
import com.ordercraft.service.BillService;
import com.ordercraft.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final BillService billService;

    public OrderController(OrderService orderService, BillService billService) {
        this.orderService = orderService;
        this.billService = billService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderCreateRequestDto request) {
        OrderResponseDto created = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(orderService.getAllOrders(status, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderUpdateRequestDto request) {
        return ResponseEntity.ok(orderService.updateOrder(id, request));
    }

    @PostMapping("/{id}/bill")
    public ResponseEntity<BillResponseDto> generateBill(@PathVariable Long id) {
        BillResponseDto bill = billService.generateBill(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(bill);
    }
}
