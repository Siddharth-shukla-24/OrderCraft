package com.ordercraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

public class TrackingDetailDto {

    @JsonProperty("order")
    private OrderResponseDto order;

    @JsonProperty("bill")
    private BillResponseDto bill;

    @JsonProperty("payments")
    private List<PaymentResponseDto> payments;

    @JsonProperty("paid_amount")
    private BigDecimal paidAmount;

    @JsonProperty("amount_due")
    private BigDecimal amountDue;

    @JsonProperty("payment_status")
    private String paymentStatus;

    public TrackingDetailDto() {}

    public TrackingDetailDto(OrderResponseDto order, BillResponseDto bill, List<PaymentResponseDto> payments, BigDecimal paidAmount, BigDecimal amountDue, String paymentStatus) {
        this.order = order;
        this.bill = bill;
        this.payments = payments;
        this.paidAmount = paidAmount;
        this.amountDue = amountDue;
        this.paymentStatus = paymentStatus;
    }

    public OrderResponseDto getOrder() {
        return order;
    }

    public void setOrder(OrderResponseDto order) {
        this.order = order;
    }

    public BillResponseDto getBill() {
        return bill;
    }

    public void setBill(BillResponseDto bill) {
        this.bill = bill;
    }

    public List<PaymentResponseDto> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentResponseDto> payments) {
        this.payments = payments;
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
