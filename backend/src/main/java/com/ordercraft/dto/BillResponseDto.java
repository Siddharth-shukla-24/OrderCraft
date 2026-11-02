package com.ordercraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ordercraft.entity.BillStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BillResponseDto {

    @JsonProperty("bill_id")
    private Long billId;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("bill_number")
    private String billNumber;

    @JsonProperty("bill_date")
    private LocalDate billDate;

    @JsonProperty("subtotal")
    private BigDecimal subtotal;

    @JsonProperty("tax_amount")
    private BigDecimal taxAmount;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("status")
    private BillStatus status;

    @JsonProperty("paid_amount")
    private BigDecimal paidAmount;

    @JsonProperty("amount_due")
    private BigDecimal amountDue;

    @JsonProperty("payment_status")
    private String paymentStatus;

    public BillResponseDto() {}

    public BillResponseDto(Long billId, Long orderId, String billNumber, LocalDate billDate, BigDecimal subtotal, BigDecimal taxAmount, BigDecimal totalAmount, BillStatus status, BigDecimal paidAmount, BigDecimal amountDue, String paymentStatus) {
        this.billId = billId;
        this.orderId = orderId;
        this.billNumber = billNumber;
        this.billDate = billDate;
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paidAmount = paidAmount;
        this.amountDue = amountDue;
        this.paymentStatus = paymentStatus;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
