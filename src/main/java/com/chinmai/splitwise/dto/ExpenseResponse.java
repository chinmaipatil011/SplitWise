package com.chinmai.splitwise.dto;

import java.math.BigDecimal;

public class ExpenseResponse {

    private Long expenseId;
    private String description;
    private BigDecimal amount;
    private String paidByName;

    public ExpenseResponse(Long expenseId, String description,
                           BigDecimal amount, String paidByName) {
        this.expenseId = expenseId;
        this.description = description;
        this.amount = amount;
        this.paidByName = paidByName;
    }

    public Long getExpenseId() {
        return expenseId;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPaidByName() {
        return paidByName;
    }
}