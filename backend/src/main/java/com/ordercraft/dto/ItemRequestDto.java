package com.ordercraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ItemRequestDto {

    @JsonProperty("order_item_id")
    private Long orderItemId;

    @NotBlank(message = "Item code is required")
    @JsonProperty("item_code")
    private String itemCode;

    @NotBlank(message = "Description is required")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.001", message = "Quantity must be greater than 0")
    @JsonProperty("quantity")
    private BigDecimal quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00", message = "Unit price cannot be negative")
    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    public ItemRequestDto() {}

    public ItemRequestDto(Long orderItemId, String itemCode, String description, BigDecimal quantity, BigDecimal unitPrice) {
        this.orderItemId = orderItemId;
        this.itemCode = itemCode;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
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
}
