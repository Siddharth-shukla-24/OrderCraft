package com.ordercraft.service;

import com.ordercraft.dto.ItemRequestDto;
import com.ordercraft.dto.OrderCreateRequestDto;
import com.ordercraft.dto.OrderResponseDto;
import com.ordercraft.dto.OrderUpdateRequestDto;
import com.ordercraft.entity.Customer;
import com.ordercraft.entity.OrderStatus;
import com.ordercraft.entity.User;
import com.ordercraft.exception.BusinessRuleException;
import com.ordercraft.repository.BillRepository;
import com.ordercraft.repository.CustomerOrderRepository;
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
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerOrderRepository orderRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillService billService;

    private Customer customer;
    private User user;

    @BeforeEach
    void setUp() {
        customer = customerRepository.findAll().stream().findFirst().orElseThrow();
        user = userRepository.findAll().stream().findFirst().orElseThrow();
    }

    @Test
    void testCreateOrderCalculatesTotalCorrectly() {
        ItemRequestDto item1 = new ItemRequestDto(null, "ITEM-1", "Test Item 1", new BigDecimal("10.000"), new BigDecimal("150.00"));
        ItemRequestDto item2 = new ItemRequestDto(null, "ITEM-2", "Test Item 2", new BigDecimal("5.000"), new BigDecimal("200.00"));

        OrderCreateRequestDto request = new OrderCreateRequestDto(
                customer.getCustomerId(),
                LocalDate.now().plusDays(7),
                user.getUserId(),
                List.of(item1, item2)
        );

        OrderResponseDto response = orderService.createOrder(request);

        assertNotNull(response.getOrderId());
        assertEquals(OrderStatus.NEW, response.getStatus());
        // 10 * 150 + 5 * 200 = 1500 + 1000 = 2500.00
        assertEquals(new BigDecimal("2500.00"), response.getTotalAmount());
        assertEquals(2, response.getItems().size());
    }

    @Test
    void testStatusTransitions() {
        ItemRequestDto item = new ItemRequestDto(null, "ITEM-1", "Test", new BigDecimal("1"), new BigDecimal("100"));
        OrderCreateRequestDto createReq = new OrderCreateRequestDto(
                customer.getCustomerId(),
                LocalDate.now().plusDays(5),
                user.getUserId(),
                List.of(item)
        );
        OrderResponseDto created = orderService.createOrder(createReq);
        Long orderId = created.getOrderId();

        // Valid transition: NEW -> CONFIRMED
        OrderResponseDto confirmed = orderService.updateOrder(orderId, new OrderUpdateRequestDto(LocalDate.now().plusDays(5), OrderStatus.CONFIRMED, null));
        assertEquals(OrderStatus.CONFIRMED, confirmed.getStatus());

        // Valid transition: CONFIRMED -> PROCESSING
        OrderResponseDto processing = orderService.updateOrder(orderId, new OrderUpdateRequestDto(LocalDate.now().plusDays(5), OrderStatus.PROCESSING, null));
        assertEquals(OrderStatus.PROCESSING, processing.getStatus());

        // Valid transition: PROCESSING -> COMPLETED
        OrderResponseDto completed = orderService.updateOrder(orderId, new OrderUpdateRequestDto(LocalDate.now().plusDays(5), OrderStatus.COMPLETED, null));
        assertEquals(OrderStatus.COMPLETED, completed.getStatus());

        // Invalid transition: COMPLETED -> NEW should throw exception
        assertThrows(BusinessRuleException.class, () ->
                orderService.updateOrder(orderId, new OrderUpdateRequestDto(LocalDate.now().plusDays(5), OrderStatus.NEW, null))
        );
    }

    @Test
    void testInvalidStatusJumpRejected() {
        ItemRequestDto item = new ItemRequestDto(null, "ITEM-1", "Test", new BigDecimal("1"), new BigDecimal("100"));
        OrderCreateRequestDto createReq = new OrderCreateRequestDto(
                customer.getCustomerId(),
                LocalDate.now().plusDays(5),
                user.getUserId(),
                List.of(item)
        );
        OrderResponseDto created = orderService.createOrder(createReq);
        Long orderId = created.getOrderId();

        // Illegal jump: NEW -> COMPLETED
        assertThrows(BusinessRuleException.class, () ->
                orderService.updateOrder(orderId, new OrderUpdateRequestDto(LocalDate.now().plusDays(5), OrderStatus.COMPLETED, null))
        );
    }

    @Test
    void testCannotModifyItemsAfterBillGenerated() {
        ItemRequestDto item = new ItemRequestDto(null, "ITEM-1", "Test", new BigDecimal("2"), new BigDecimal("500"));
        OrderCreateRequestDto createReq = new OrderCreateRequestDto(
                customer.getCustomerId(),
                LocalDate.now().plusDays(5),
                user.getUserId(),
                List.of(item)
        );
        OrderResponseDto created = orderService.createOrder(createReq);
        Long orderId = created.getOrderId();

        // Generate bill
        billService.generateBill(orderId);

        // Attempt to update items should fail
        ItemRequestDto newItem = new ItemRequestDto(null, "ITEM-2", "New Item", new BigDecimal("3"), new BigDecimal("300"));
        assertThrows(BusinessRuleException.class, () ->
                orderService.updateOrder(orderId, new OrderUpdateRequestDto(LocalDate.now().plusDays(5), OrderStatus.NEW, List.of(newItem)))
        );
    }
}
