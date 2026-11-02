package com.ordercraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ordercraft.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrderResponseDto {

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("created_by_name")
    private String createdByName;

    @JsonProperty("order_date")
    private LocalDate orderDate;

    @JsonProperty("required_date")
    private LocalDate requiredDate;

    @JsonProperty("status")
    private OrderStatus status;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("items")
    private List<OrderItemResponseDto> items;

    @JsonProperty("bill")
    private BillResponseDto bill;

    public OrderResponseDto() {}

    public OrderResponseDto(Long orderId, Long customerId, String customerName, Long createdBy, String createdByName, LocalDate orderDate, LocalDate requiredDate, OrderStatus status, BigDecimal totalAmount, List<OrderItemResponseDto> items, BillResponseDto bill) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.orderDate = orderDate;
        this.requiredDate = requiredDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.items = items;
        this.bill = bill;
    }

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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(LocalDate requiredDate) {
        this.requiredDate = requiredDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItemResponseDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponseDto> items) {
        this.items = items;
    }

    public BillResponseDto getBill() {
        return bill;
    }

    public void setBill(BillResponseDto bill) {
        this.bill = bill;
    }
}
