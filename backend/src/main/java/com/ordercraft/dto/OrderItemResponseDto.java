package com.ordercraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class OrderItemResponseDto {

    @JsonProperty("order_item_id")
    private Long orderItemId;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("item_code")
    private String itemCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("quantity")
    private BigDecimal quantity;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @JsonProperty("line_total")
    private BigDecimal lineTotal;

    public OrderItemResponseDto() {}

    public OrderItemResponseDto(Long orderItemId, Long orderId, String itemCode, String description, BigDecimal quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.itemCode = itemCode;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}
