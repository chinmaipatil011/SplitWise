package com.chinmai.splitwise.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SettlementRequest {

    @NotNull
    private Long groupId;

    @NotNull
    private Long paidByUserId;

    @NotNull
    private Long paidToUserId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getPaidByUserId() {
        return paidByUserId;
    }

    public void setPaidByUserId(Long paidByUserId) {
        this.paidByUserId = paidByUserId;
    }

    public Long getPaidToUserId() {
        return paidToUserId;
    }

    public void setPaidToUserId(Long paidToUserId) {
        this.paidToUserId = paidToUserId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}