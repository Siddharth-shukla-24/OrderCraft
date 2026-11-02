package com.ordercraft.service;

import com.ordercraft.dto.BillResponseDto;
import com.ordercraft.dto.ItemRequestDto;
import com.ordercraft.dto.OrderCreateRequestDto;
import com.ordercraft.dto.OrderResponseDto;
import com.ordercraft.entity.BillStatus;
import com.ordercraft.entity.Customer;
import com.ordercraft.entity.User;
import com.ordercraft.exception.BusinessRuleException;
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
class BillServiceTest {

    @Autowired
    private BillService billService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    private Long orderId;

    @BeforeEach
    void setUp() {
        Customer customer = customerRepository.findAll().stream().findFirst().orElseThrow();
        User user = userRepository.findAll().stream().findFirst().orElseThrow();

        ItemRequestDto item = new ItemRequestDto(null, "IT-99", "Machined Part", new BigDecimal("4"), new BigDecimal("250.00"));
        OrderResponseDto order = orderService.createOrder(new OrderCreateRequestDto(
                customer.getCustomerId(),
                LocalDate.now().plusDays(10),
                user.getUserId(),
                List.of(item)
        ));
        orderId = order.getOrderId();
    }

    @Test
    void testGenerateBillZeroTaxAndOneBillPerOrder() {
        BillResponseDto bill = billService.generateBill(orderId);

        assertNotNull(bill.getBillId());
        assertTrue(bill.getBillNumber().startsWith("BILL-"));
        assertEquals(new BigDecimal("1000.00"), bill.getSubtotal());
        assertEquals(new BigDecimal("0.00"), bill.getTaxAmount());
        assertEquals(new BigDecimal("1000.00"), bill.getTotalAmount());
        assertEquals(BillStatus.GENERATED, bill.getStatus());

        // Attempting to generate a second bill for the same order must fail (1 bill per order rule)
        assertThrows(BusinessRuleException.class, () -> billService.generateBill(orderId));
    }

    @Test
    void testIssueBill() {
        BillResponseDto bill = billService.generateBill(orderId);
        assertEquals(BillStatus.GENERATED, bill.getStatus());

        BillResponseDto issued = billService.issueBill(bill.getBillId());
        assertEquals(BillStatus.ISSUED, issued.getStatus());

        // Issuing an already issued bill must fail
        assertThrows(BusinessRuleException.class, () -> billService.issueBill(bill.getBillId()));
    }
}
