package com.chinmai.splitwise.dto;

import java.math.BigDecimal;

public class BalanceResponse {

    private String userName;
    private BigDecimal netBalance;

    public BalanceResponse(String userName, BigDecimal netBalance) {
        this.userName = userName;
        this.netBalance = netBalance;
    }

    public String getUserName() {
        return userName;
    }

    public BigDecimal getNetBalance() {
        return netBalance;
    }
}