package com.ordercraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class OrderCreateRequestDto {

    @NotNull(message = "Customer ID is required")
    @JsonProperty("customer_id")
    private Long customerId;

    @NotNull(message = "Required date is required")
    @JsonProperty("required_date")
    private LocalDate requiredDate;

    @JsonProperty("created_by")
    private Long createdBy;

    @NotEmpty(message = "Order must have at least one line item")
    @Valid
    @JsonProperty("items")
    private List<ItemRequestDto> items;

    public OrderCreateRequestDto() {}

    public OrderCreateRequestDto(Long customerId, LocalDate requiredDate, Long createdBy, List<ItemRequestDto> items) {
        this.customerId = customerId;
        this.requiredDate = requiredDate;
        this.createdBy = createdBy;
        this.items = items;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDate getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(LocalDate requiredDate) {
        this.requiredDate = requiredDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public List<ItemRequestDto> getItems() {
        return items;
    }

    public void setItems(List<ItemRequestDto> items) {
        this.items = items;
    }
}
