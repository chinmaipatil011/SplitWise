package com.chinmai.splitwise.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "settlements")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long settlementId;

    @ManyToOne
    @JoinColumn(name = "paid_by", nullable = false)
    private User paidBy;

    @ManyToOne
    @JoinColumn(name = "paid_to", nullable = false)
    private User paidTo;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    private LocalDateTime settledAt;

    public Settlement() {}

    public Long getSettlementId() {
        return settlementId;
    }

    public User getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(User paidBy) {
        this.paidBy = paidBy;
    }

    public User getPaidTo() {
        return paidTo;
    }

    public void setPaidTo(User paidTo) {
        this.paidTo = paidTo;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getSettledAt() {
        return settledAt;
    }

    @PrePersist
    protected void onCreate() {
        settledAt = LocalDateTime.now();
    }
}
