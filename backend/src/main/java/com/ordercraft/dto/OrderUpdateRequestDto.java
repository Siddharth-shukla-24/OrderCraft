package com.ordercraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ordercraft.entity.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class OrderUpdateRequestDto {

    @NotNull(message = "Required date is required")
    @JsonProperty("required_date")
    private LocalDate requiredDate;

    @NotNull(message = "Status is required")
    @JsonProperty("status")
    private OrderStatus status;

    @Valid
    @JsonProperty("items")
    private List<ItemRequestDto> items;

    public OrderUpdateRequestDto() {}

    public OrderUpdateRequestDto(LocalDate requiredDate, OrderStatus status, List<ItemRequestDto> items) {
        this.requiredDate = requiredDate;
        this.status = status;
        this.items = items;
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

    public List<ItemRequestDto> getItems() {
        return items;
    }

    public void setItems(List<ItemRequestDto> items) {
        this.items = items;
    }
}
