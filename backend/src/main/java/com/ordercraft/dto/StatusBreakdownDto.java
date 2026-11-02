package com.ordercraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ordercraft.entity.OrderStatus;

public class StatusBreakdownDto {

    @JsonProperty("status")
    private OrderStatus status;

    @JsonProperty("count")
    private long count;

    @JsonProperty("percent")
    private int percent;

    public StatusBreakdownDto() {}

    public StatusBreakdownDto(OrderStatus status, long count, int percent) {
        this.status = status;
        this.count = count;
        this.percent = percent;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
