package com.ordercraft.service;

import com.ordercraft.dto.PaymentRequestDto;
import com.ordercraft.dto.PaymentResponseDto;
import com.ordercraft.entity.Bill;
import com.ordercraft.entity.Payment;
import com.ordercraft.exception.BusinessRuleException;
import com.ordercraft.exception.ResourceNotFoundException;
import com.ordercraft.repository.BillRepository;
import com.ordercraft.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;

    public PaymentService(PaymentRepository paymentRepository, BillRepository billRepository) {
        this.paymentRepository = paymentRepository;
        this.billRepository = billRepository;
    }

    @Transactional
    public PaymentResponseDto recordPayment(Long billId, PaymentRequestDto request) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + billId));

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Amount must be greater than 0");
        }

        BigDecimal currentPaid = calculatePaidAmount(billId);
        BigDecimal amountDue = bill.getTotalAmount().subtract(currentPaid).setScale(2, RoundingMode.HALF_UP);

        if (request.getAmount().compareTo(amountDue) > 0) {
            throw new BusinessRuleException("Amount cannot exceed the amount due (" + amountDue + ")");
        }

        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setPaymentDate(request.getPaymentDate());
        payment.setAmount(request.getAmount().setScale(2, RoundingMode.HALF_UP));
        payment.setMethod(request.getMethod().trim());
        payment.setReferenceNo(request.getReferenceNo() != null ? request.getReferenceNo().trim() : "");
        payment.setStatus("RECORDED");

        Payment saved = paymentRepository.save(payment);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByBillId(Long billId) {
        return paymentRepository.findByBillBillIdOrderByPaymentDateDescPaymentIdDesc(billId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal calculatePaidAmount(Long billId) {
        return paymentRepository.findByBillBillId(billId)
                .stream()
                .filter(p -> p.getAmount() != null && p.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateAmountDue(Bill bill) {
        BigDecimal paid = calculatePaidAmount(bill.getBillId());
        BigDecimal due = bill.getTotalAmount().subtract(paid).setScale(2, RoundingMode.HALF_UP);
        return due.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : due;
    }

    public String calculatePaymentStatus(Bill bill) {
        BigDecimal paid = calculatePaidAmount(bill.getBillId());
        if (paid.compareTo(BigDecimal.ZERO) <= 0) {
            return "PENDING";
        }
        if (paid.compareTo(bill.getTotalAmount()) >= 0) {
            return "PAID";
        }
        return "PARTIALLY PAID";
    }

    public PaymentResponseDto toDto(Payment p) {
        return new PaymentResponseDto(
                p.getPaymentId(),
                p.getBill().getBillId(),
                p.getPaymentDate(),
                p.getAmount(),
                p.getMethod(),
                p.getReferenceNo(),
                p.getStatus()
        );
    }
}
