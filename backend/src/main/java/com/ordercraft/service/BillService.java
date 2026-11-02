package com.ordercraft.service;

import com.ordercraft.dto.BillResponseDto;
import com.ordercraft.entity.Bill;
import com.ordercraft.entity.BillStatus;
import com.ordercraft.entity.CustomerOrder;
import com.ordercraft.exception.BusinessRuleException;
import com.ordercraft.exception.ResourceNotFoundException;
import com.ordercraft.repository.BillRepository;
import com.ordercraft.repository.CustomerOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final CustomerOrderRepository orderRepository;
    private final PaymentService paymentService;

    public BillService(BillRepository billRepository, CustomerOrderRepository orderRepository, PaymentService paymentService) {
        this.billRepository = billRepository;
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public BillResponseDto generateBill(Long orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (billRepository.existsByOrderOrderId(orderId)) {
            throw new BusinessRuleException("This order already has a bill.");
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new BusinessRuleException("The order has no line items.");
        }

        BigDecimal subtotal = order.getTotalAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(taxAmount);

        long billCount = billRepository.count();
        int year = LocalDate.now().getYear();
        String billNumber = String.format("BILL-%d-%04d", year, billCount + 1);

        int counter = 1;
        while (billRepository.existsByBillNumber(billNumber)) {
            billNumber = String.format("BILL-%d-%04d", year, billCount + counter);
            counter++;
        }

        Bill bill = new Bill();
        bill.setOrder(order);
        bill.setBillNumber(billNumber);
        bill.setBillDate(LocalDate.now());
        bill.setSubtotal(subtotal);
        bill.setTaxAmount(taxAmount);
        bill.setTotalAmount(totalAmount);
        bill.setStatus(BillStatus.GENERATED);

        Bill saved = billRepository.save(bill);
        return toDto(saved);
    }

    @Transactional
    public BillResponseDto issueBill(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + billId));

        if (bill.getStatus() != BillStatus.GENERATED) {
            throw new BusinessRuleException("Only GENERATED bills can be issued");
        }

        bill.setStatus(BillStatus.ISSUED);
        Bill saved = billRepository.save(bill);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public BillResponseDto getBillById(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + billId));
        return toDto(bill);
    }

    @Transactional(readOnly = true)
    public Optional<BillResponseDto> getBillByOrderId(Long orderId) {
        return billRepository.findByOrderOrderId(orderId).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<BillResponseDto> getAllBills(BillStatus status, String search) {
        List<Bill> list = billRepository.findAllByOrderByBillDateDescBillIdDesc();

        return list.stream()
                .filter(b -> status == null || b.getStatus() == status)
                .filter(b -> {
                    if (search == null || search.isBlank()) return true;
                    String q = search.trim().toLowerCase().replace("#", "");
                    return b.getBillNumber().toLowerCase().contains(q)
                            || String.valueOf(b.getOrder().getOrderId()).contains(q)
                            || (b.getOrder().getCustomer() != null && b.getOrder().getCustomer().getCustomerName().toLowerCase().contains(q));
                })
                .map(this::toDto)
                .toList();
    }

    public BillResponseDto toDto(Bill b) {
        BigDecimal paid = paymentService.calculatePaidAmount(b.getBillId());
        BigDecimal due = paymentService.calculateAmountDue(b);
        String paymentStatus = paymentService.calculatePaymentStatus(b);

        return new BillResponseDto(
                b.getBillId(),
                b.getOrder().getOrderId(),
                b.getBillNumber(),
                b.getBillDate(),
                b.getSubtotal(),
                b.getTaxAmount(),
                b.getTotalAmount(),
                b.getStatus(),
                paid,
                due,
                paymentStatus
        );
    }
}
