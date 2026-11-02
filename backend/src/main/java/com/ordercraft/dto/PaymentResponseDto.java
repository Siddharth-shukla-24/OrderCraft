package com.ordercraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentResponseDto {

    @JsonProperty("payment_id")
    private Long paymentId;

    @JsonProperty("bill_id")
    private Long billId;

    @JsonProperty("payment_date")
    private LocalDate paymentDate;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("method")
    private String method;

    @JsonProperty("reference_no")
    private String referenceNo;

    @JsonProperty("status")
    private String status;

    public PaymentResponseDto() {}

    public PaymentResponseDto(Long paymentId, Long billId, LocalDate paymentDate, BigDecimal amount, String method, String referenceNo, String status) {
        this.paymentId = paymentId;
        this.billId = billId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.method = method;
        this.referenceNo = referenceNo;
        this.status = status;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
