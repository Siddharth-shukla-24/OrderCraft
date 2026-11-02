package com.ordercraft.service;

import com.ordercraft.dto.*;
import com.ordercraft.entity.Bill;
import com.ordercraft.entity.Customer;
import com.ordercraft.entity.User;
import com.ordercraft.exception.BusinessRuleException;
import com.ordercraft.repository.BillRepository;
import com.ordercraft.repository.CustomerRepository;
import com.ordercraft.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BillService billService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillRepository billRepository;

    private Long billId;

    @BeforeEach
    void setUp() {
        Customer customer = customerRepository.findAll().stream().findFirst().orElseThrow();
        User user = userRepository.findAll().stream().findFirst().orElseThrow();

        ItemRequestDto item = new ItemRequestDto(null, "IT-10", "Assembly Unit", new BigDecimal("2"), new BigDecimal("1000.00"));
        OrderResponseDto order = orderService.createOrder(new OrderCreateRequestDto(
                customer.getCustomerId(),
                LocalDate.now().plusDays(10),
                user.getUserId(),
                List.of(item)
        ));
        BillResponseDto bill = billService.generateBill(order.getOrderId());
        billId = bill.getBillId();
    }

    @Test
    void testPartialAndFullPaymentFlow() {
        Bill bill = billRepository.findById(billId).orElseThrow();

        // Initial state
        assertEquals("PENDING", paymentService.calculatePaymentStatus(bill));
        assertEquals(new BigDecimal("0.00"), paymentService.calculatePaidAmount(billId));
        assertEquals(new BigDecimal("2000.00"), paymentService.calculateAmountDue(bill));

        // Partial payment 1: 500
        PaymentResponseDto p1 = paymentService.recordPayment(billId, new PaymentRequestDto(billId, LocalDate.now(), new BigDecimal("500.00"), "Bank transfer", "REF-1"));
        assertEquals("RECORDED", p1.getStatus());
        assertEquals(new BigDecimal("500.00"), paymentService.calculatePaidAmount(billId));
        assertEquals(new BigDecimal("1500.00"), paymentService.calculateAmountDue(bill));
        assertEquals("PARTIALLY PAID", paymentService.calculatePaymentStatus(bill));

        // Partial payment 2: 1500 (full remaining amount)
        PaymentResponseDto p2 = paymentService.recordPayment(billId, new PaymentRequestDto(billId, LocalDate.now(), new BigDecimal("1500.00"), "Cheque", "REF-2"));
        assertEquals("RECORDED", p2.getStatus());
        assertEquals(new BigDecimal("2000.00"), paymentService.calculatePaidAmount(billId));
        assertEquals(new BigDecimal("0.00"), paymentService.calculateAmountDue(bill));
        assertEquals("PAID", paymentService.calculatePaymentStatus(bill));
    }

    @Test
    void testOverpaymentRejected() {
        // Bill total is 2000. Recording 2000.01 should fail
        assertThrows(BusinessRuleException.class, () ->
                paymentService.recordPayment(billId, new PaymentRequestDto(billId, LocalDate.now(), new BigDecimal("2000.01"), "Cash", "REF-X"))
        );
    }

    @Test
    void testPositiveAmountValidation() {
        // Zero or negative amounts must fail
        assertThrows(Exception.class, () ->
                paymentService.recordPayment(billId, new PaymentRequestDto(billId, LocalDate.now(), new BigDecimal("0.00"), "Cash", "REF-0"))
        );
        assertThrows(Exception.class, () ->
                paymentService.recordPayment(billId, new PaymentRequestDto(billId, LocalDate.now(), new BigDecimal("-50.00"), "Cash", "REF-NEG"))
        );
    }
}
