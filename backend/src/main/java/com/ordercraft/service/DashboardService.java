package com.ordercraft.service;

import com.ordercraft.dto.DashboardDto;
import com.ordercraft.dto.StatusBreakdownDto;
import com.ordercraft.dto.TrackingSummaryDto;
import com.ordercraft.entity.Bill;
import com.ordercraft.entity.CustomerOrder;
import com.ordercraft.entity.OrderStatus;
import com.ordercraft.repository.BillRepository;
import com.ordercraft.repository.CustomerOrderRepository;
import com.ordercraft.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final CustomerOrderRepository orderRepository;
    private final BillRepository billRepository;
    private final SupplierRepository supplierRepository;
    private final PaymentService paymentService;
    private final TrackingService trackingService;

    public DashboardService(CustomerOrderRepository orderRepository,
                            BillRepository billRepository,
                            SupplierRepository supplierRepository,
                            PaymentService paymentService,
                            TrackingService trackingService) {
        this.orderRepository = orderRepository;
        this.billRepository = billRepository;
        this.supplierRepository = supplierRepository;
        this.paymentService = paymentService;
        this.trackingService = trackingService;
    }

    public DashboardDto getDashboard() {
        long totalOrders = orderRepository.count();
        long openOrders = orderRepository.countByStatusNot(OrderStatus.COMPLETED);

        List<Bill> allBills = billRepository.findAll();
        BigDecimal outstandingPayments = allBills.stream()
                .map(paymentService::calculateAmountDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        long activeSuppliers = supplierRepository.countByStatus("ACTIVE");

        List<StatusBreakdownDto> breakdown = new ArrayList<>();
        for (OrderStatus status : OrderStatus.values()) {
            long count = orderRepository.countByStatus(status);
            int percent = totalOrders > 0 ? (int) Math.round(((double) count / totalOrders) * 100) : 0;
            breakdown.add(new StatusBreakdownDto(status, count, percent));
        }

        List<TrackingSummaryDto> allTracking = trackingService.getTrackingSummaries(null, null, null);
        List<TrackingSummaryDto> recent = allTracking.stream()
                .sorted((a, b) -> {
                    int dateComp = b.getOrderDate().compareTo(a.getOrderDate());
                    return dateComp != 0 ? dateComp : Long.compare(b.getOrderId(), a.getOrderId());
                })
                .limit(5)
                .toList();

        return new DashboardDto(
                totalOrders,
                openOrders,
                outstandingPayments,
                activeSuppliers,
                breakdown,
                recent
        );
    }
}
