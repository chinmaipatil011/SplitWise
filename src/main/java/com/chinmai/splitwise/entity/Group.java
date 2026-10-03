package com.chinmai.splitwise.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

    @Entity 

    @Table(name = "groups")

public class Group {
    @Id 
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long groupId;

    @Column(nullable= false)
    private String name;

    @ManyToOne 
    @JoinColumn(name = "created_By")
    private User createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Group() {

    }

    public Long getGroupId() {
        return groupId;
    }


    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PrePersist 
    protected void onCreate() {
        createdAt=LocalDateTime.now();
        updatedAt=LocalDateTime.now();
    }

    @PreUpdate 
    protected void onUpdate() {
        updatedAt=LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
