package com.ordercraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

public class DashboardDto {

    @JsonProperty("total_orders")
    private long totalOrders;

    @JsonProperty("open_orders")
    private long openOrders;

    @JsonProperty("outstanding_payments")
    private BigDecimal outstandingPayments;

    @JsonProperty("active_suppliers")
    private long activeSuppliers;

    @JsonProperty("status_breakdown")
    private List<StatusBreakdownDto> statusBreakdown;

    @JsonProperty("recent_orders")
    private List<TrackingSummaryDto> recentOrders;

    public DashboardDto() {}

    public DashboardDto(long totalOrders, long openOrders, BigDecimal outstandingPayments, long activeSuppliers, List<StatusBreakdownDto> statusBreakdown, List<TrackingSummaryDto> recentOrders) {
        this.totalOrders = totalOrders;
        this.openOrders = openOrders;
        this.outstandingPayments = outstandingPayments;
        this.activeSuppliers = activeSuppliers;
        this.statusBreakdown = statusBreakdown;
        this.recentOrders = recentOrders;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getOpenOrders() {
        return openOrders;
    }

    public void setOpenOrders(long openOrders) {
        this.openOrders = openOrders;
    }

    public BigDecimal getOutstandingPayments() {
        return outstandingPayments;
    }

    public void setOutstandingPayments(BigDecimal outstandingPayments) {
        this.outstandingPayments = outstandingPayments;
    }

    public long getActiveSuppliers() {
        return activeSuppliers;
    }

    public void setActiveSuppliers(long activeSuppliers) {
        this.activeSuppliers = activeSuppliers;
    }

    public List<StatusBreakdownDto> getStatusBreakdown() {
        return statusBreakdown;
    }

    public void setStatusBreakdown(List<StatusBreakdownDto> statusBreakdown) {
        this.statusBreakdown = statusBreakdown;
    }

    public List<TrackingSummaryDto> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<TrackingSummaryDto> recentOrders) {
        this.recentOrders = recentOrders;
    }
}
