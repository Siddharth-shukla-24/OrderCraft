package com.ordercraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ordercraft.entity.BillStatus;
import com.ordercraft.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TrackingSummaryDto {

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("order_status")
    private OrderStatus orderStatus;

    @JsonProperty("order_date")
    private LocalDate orderDate;

    @JsonProperty("order_total")
    private BigDecimal orderTotal;

    @JsonProperty("bill_id")
    private Long billId;

    @JsonProperty("bill_number")
    private String billNumber;

    @JsonProperty("bill_status")
    private BillStatus billStatus;

    @JsonProperty("bill_total")
    private BigDecimal billTotal;

    @JsonProperty("paid_amount")
    private BigDecimal paidAmount;

    @JsonProperty("amount_due")
    private BigDecimal amountDue;

    @JsonProperty("payment_status")
    private String paymentStatus;

    public TrackingSummaryDto() {}

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public BillStatus getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(BillStatus billStatus) {
        this.billStatus = billStatus;
    }

    public BigDecimal getBillTotal() {
        return billTotal;
    }

    public void setBillTotal(BigDecimal billTotal) {
        this.billTotal = billTotal;
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
