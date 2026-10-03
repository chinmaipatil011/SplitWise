package com.chinmai.splitwise.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ExpenseRequest {

    @NotNull
    private Long groupId;

    @NotNull
    private Long paidByUserId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotBlank
    private String description;

    @NotEmpty
    private List<Long> splitBetweenUserIds;

    public ExpenseRequest() {}

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getSplitBetweenUserIds() {
        return splitBetweenUserIds;
    }

    public void setSplitBetweenUserIds(List<Long> splitBetweenUserIds) {
        this.splitBetweenUserIds = splitBetweenUserIds;
    }
}