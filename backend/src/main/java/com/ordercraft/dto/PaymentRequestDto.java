package com.ordercraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentRequestDto {

    @JsonProperty("bill_id")
    private Long billId;

    @NotNull(message = "Payment date is required")
    @JsonProperty("payment_date")
    private LocalDate paymentDate;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @JsonProperty("amount")
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required")
    @JsonProperty("method")
    private String method;

    @JsonProperty("reference_no")
    private String referenceNo;

    public PaymentRequestDto() {}

    public PaymentRequestDto(Long billId, LocalDate paymentDate, BigDecimal amount, String method, String referenceNo) {
        this.billId = billId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.method = method;
        this.referenceNo = referenceNo;
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
}
