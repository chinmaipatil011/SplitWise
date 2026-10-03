package com.chinmai.splitwise.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
// Calls JPA/Hibernate and tells it this class is an entity
// that should be mapped to the database.

@Table(name = "users")
// Maps this entity to the "users" table.

public class User {

    @Id
    // Primary key

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // When a new User is inserted, let the database generate the ID

    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable=false)
    private String passwordHash;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public User() {
    }
    // JPA/Hibernate needs a no-argument constructor
    // so it can create User objects when retrieving data.

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate()
    {
        updatedAt = LocalDateTime.now();
    }
}