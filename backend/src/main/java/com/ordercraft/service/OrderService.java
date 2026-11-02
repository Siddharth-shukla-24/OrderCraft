package com.ordercraft.service;

import com.ordercraft.dto.*;
import com.ordercraft.entity.*;
import com.ordercraft.exception.BusinessRuleException;
import com.ordercraft.exception.ResourceNotFoundException;
import com.ordercraft.repository.BillRepository;
import com.ordercraft.repository.CustomerOrderRepository;
import com.ordercraft.repository.CustomerRepository;
import com.ordercraft.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final BillRepository billRepository;
    private final BillService billService;

    public OrderService(CustomerOrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        UserRepository userRepository,
                        BillRepository billRepository,
                        BillService billService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.billRepository = billRepository;
        this.billService = billService;
    }

    @Transactional
    public OrderResponseDto createOrder(OrderCreateRequestDto request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        User createdBy;
        if (request.getCreatedBy() != null) {
            createdBy = userRepository.findById(request.getCreatedBy())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getCreatedBy()));
        } else {
            createdBy = userRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new BusinessRuleException("No user found in the system to attribute order creation"));
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessRuleException("Order must contain at least one line item");
        }

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setCreatedBy(createdBy);
        order.setOrderDate(LocalDate.now());
        order.setRequiredDate(request.getRequiredDate());
        order.setStatus(OrderStatus.NEW);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();
        for (ItemRequestDto itemDto : request.getItems()) {
            validateItem(itemDto);
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setItemCode(itemDto.getItemCode().trim());
            item.setDescription(itemDto.getDescription().trim());
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(itemDto.getUnitPrice().setScale(2, RoundingMode.HALF_UP));
            BigDecimal lineTotal = item.getQuantity().multiply(item.getUnitPrice()).setScale(2, RoundingMode.HALF_UP);
            item.setLineTotal(lineTotal);
            total = total.add(lineTotal);
            items.add(item);
        }

        order.setTotalAmount(total.setScale(2, RoundingMode.HALF_UP));
        order.setItems(items);

        CustomerOrder saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Transactional
    public OrderResponseDto updateOrder(Long orderId, OrderUpdateRequestDto request) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        validateStatusTransition(order.getStatus(), request.getStatus());
        order.setStatus(request.getStatus());
        order.setRequiredDate(request.getRequiredDate());

        boolean hasBill = billRepository.existsByOrderOrderId(orderId);
        if (request.getItems() != null) {
            if (hasBill) {
                throw new BusinessRuleException("Line items are locked because a bill has already been generated for this order");
            }

            if (request.getItems().isEmpty()) {
                throw new BusinessRuleException("Order must have at least one line item");
            }

            order.getItems().clear();
            BigDecimal total = BigDecimal.ZERO;
            for (ItemRequestDto itemDto : request.getItems()) {
                validateItem(itemDto);
                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setItemCode(itemDto.getItemCode().trim());
                item.setDescription(itemDto.getDescription().trim());
                item.setQuantity(itemDto.getQuantity());
                item.setUnitPrice(itemDto.getUnitPrice().setScale(2, RoundingMode.HALF_UP));
                BigDecimal lineTotal = item.getQuantity().multiply(item.getUnitPrice()).setScale(2, RoundingMode.HALF_UP);
                item.setLineTotal(lineTotal);
                total = total.add(lineTotal);
                order.getItems().add(item);
            }
            order.setTotalAmount(total.setScale(2, RoundingMode.HALF_UP));
        }

        CustomerOrder saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return toDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders(OrderStatus status, String search) {
        List<CustomerOrder> orders = orderRepository.findAllByOrderByOrderDateDescOrderIdDesc();

        return orders.stream()
                .filter(o -> status == null || o.getStatus() == status)
                .filter(o -> {
                    if (search == null || search.isBlank()) return true;
                    String q = search.trim().toLowerCase().replace("#", "");
                    boolean matchesId = String.valueOf(o.getOrderId()).contains(q);
                    boolean matchesCust = o.getCustomer() != null && o.getCustomer().getCustomerName().toLowerCase().contains(q);
                    boolean matchesBill = billRepository.findByOrderOrderId(o.getOrderId())
                            .map(b -> b.getBillNumber().toLowerCase().contains(q))
                            .orElse(false);
                    return matchesId || matchesCust || matchesBill;
                })
                .map(this::toDto)
                .toList();
    }

    public void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == next) {
            return;
        }

        boolean valid = switch (current) {
            case NEW -> next == OrderStatus.CONFIRMED;
            case CONFIRMED -> next == OrderStatus.PROCESSING;
            case PROCESSING -> next == OrderStatus.COMPLETED;
            case COMPLETED -> false;
        };

        if (!valid) {
            throw new BusinessRuleException(
                    "Invalid order status transition from " + current + " to " + next +
                    ". Allowed transition is only to the immediate next stage (NEW -> CONFIRMED -> PROCESSING -> COMPLETED)."
            );
        }
    }

    private void validateItem(ItemRequestDto item) {
        if (item.getItemCode() == null || item.getItemCode().isBlank()) {
            throw new BusinessRuleException("Item code is required");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new BusinessRuleException("Description is required");
        }
        if (item.getQuantity() == null || item.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Quantity must be greater than 0");
        }
        if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("Unit price cannot be negative");
        }
    }

    public OrderResponseDto toDto(CustomerOrder o) {
        List<OrderItemResponseDto> itemDtos = o.getItems().stream()
                .map(i -> new OrderItemResponseDto(
                        i.getOrderItemId(),
                        o.getOrderId(),
                        i.getItemCode(),
                        i.getDescription(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getLineTotal()
                ))
                .toList();

        BillResponseDto billDto = billRepository.findByOrderOrderId(o.getOrderId())
                .map(billService::toDto)
                .orElse(null);

        return new OrderResponseDto(
                o.getOrderId(),
                o.getCustomer() != null ? o.getCustomer().getCustomerId() : null,
                o.getCustomer() != null ? o.getCustomer().getCustomerName() : "Unknown",
                o.getCreatedBy() != null ? o.getCreatedBy().getUserId() : null,
                o.getCreatedBy() != null ? o.getCreatedBy().getFullName() : "—",
                o.getOrderDate(),
                o.getRequiredDate(),
                o.getStatus(),
                o.getTotalAmount(),
                itemDtos,
                billDto
        );
    }
}
