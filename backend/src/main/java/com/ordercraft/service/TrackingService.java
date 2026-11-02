package com.ordercraft.service;

import com.ordercraft.dto.*;
import com.ordercraft.entity.Bill;
import com.ordercraft.entity.CustomerOrder;
import com.ordercraft.entity.OrderStatus;
import com.ordercraft.exception.ResourceNotFoundException;
import com.ordercraft.repository.BillRepository;
import com.ordercraft.repository.CustomerOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TrackingService {

    private final CustomerOrderRepository orderRepository;
    private final BillRepository billRepository;
    private final BillService billService;
    private final PaymentService paymentService;
    private final OrderService orderService;

    public TrackingService(CustomerOrderRepository orderRepository,
                           BillRepository billRepository,
                           BillService billService,
                           PaymentService paymentService,
                           OrderService orderService) {
        this.orderRepository = orderRepository;
        this.billRepository = billRepository;
        this.billService = billService;
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    public List<TrackingSummaryDto> getTrackingSummaries(OrderStatus orderStatus, String paymentFilter, String search) {
        List<CustomerOrder> orders = orderRepository.findAllByOrderByOrderDateDescOrderIdDesc();
        List<TrackingSummaryDto> summaries = new ArrayList<>();

        for (CustomerOrder o : orders) {
            Optional<Bill> billOpt = billRepository.findByOrderOrderId(o.getOrderId());
            BigDecimal paid = billOpt.map(b -> paymentService.calculatePaidAmount(b.getBillId())).orElse(BigDecimal.ZERO);
            BigDecimal due = billOpt.map(paymentService::calculateAmountDue).orElse(BigDecimal.ZERO);
            String payStatus = billOpt.map(paymentService::calculatePaymentStatus).orElse(null);

            TrackingSummaryDto dto = new TrackingSummaryDto();
            dto.setOrderId(o.getOrderId());
            dto.setCustomerId(o.getCustomer() != null ? o.getCustomer().getCustomerId() : null);
            dto.setCustomerName(o.getCustomer() != null ? o.getCustomer().getCustomerName() : "Unknown");
            dto.setOrderStatus(o.getStatus());
            dto.setOrderDate(o.getOrderDate());
            dto.setOrderTotal(o.getTotalAmount());

            if (billOpt.isPresent()) {
                Bill b = billOpt.get();
                dto.setBillId(b.getBillId());
                dto.setBillNumber(b.getBillNumber());
                dto.setBillStatus(b.getStatus());
                dto.setBillTotal(b.getTotalAmount());
                dto.setPaidAmount(paid);
                dto.setAmountDue(due);
                dto.setPaymentStatus(payStatus);
            } else {
                dto.setBillId(null);
                dto.setBillNumber(null);
                dto.setBillStatus(null);
                dto.setBillTotal(BigDecimal.ZERO);
                dto.setPaidAmount(BigDecimal.ZERO);
                dto.setAmountDue(BigDecimal.ZERO);
                dto.setPaymentStatus(null);
            }

            summaries.add(dto);
        }

        return summaries.stream()
                .filter(s -> orderStatus == null || s.getOrderStatus() == orderStatus)
                .filter(s -> {
                    if (paymentFilter == null || paymentFilter.isBlank() || "ALL".equalsIgnoreCase(paymentFilter)) {
                        return true;
                    }
                    if ("NO BILL".equalsIgnoreCase(paymentFilter)) {
                        return s.getPaymentStatus() == null;
                    }
                    return paymentFilter.equalsIgnoreCase(s.getPaymentStatus());
                })
                .filter(s -> {
                    if (search == null || search.isBlank()) return true;
                    String q = search.trim().toLowerCase().replace("#", "");
                    boolean matchesId = String.valueOf(s.getOrderId()).contains(q);
                    boolean matchesCust = s.getCustomerName().toLowerCase().contains(q);
                    boolean matchesBill = s.getBillNumber() != null && s.getBillNumber().toLowerCase().contains(q);
                    return matchesId || matchesCust || matchesBill;
                })
                .toList();
    }

    public TrackingDetailDto getTrackingDetail(Long orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        OrderResponseDto orderDto = orderService.toDto(order);
        Optional<Bill> billOpt = billRepository.findByOrderOrderId(orderId);

        BillResponseDto billDto = null;
        List<PaymentResponseDto> payments = List.of();
        BigDecimal paid = BigDecimal.ZERO;
        BigDecimal due = BigDecimal.ZERO;
        String paymentStatus = null;

        if (billOpt.isPresent()) {
            Bill b = billOpt.get();
            billDto = billService.toDto(b);
            payments = paymentService.getPaymentsByBillId(b.getBillId());
            paid = paymentService.calculatePaidAmount(b.getBillId());
            due = paymentService.calculateAmountDue(b);
            paymentStatus = paymentService.calculatePaymentStatus(b);
        }

        return new TrackingDetailDto(
                orderDto,
                billDto,
                payments,
                paid,
                due,
                paymentStatus
        );
    }
}
