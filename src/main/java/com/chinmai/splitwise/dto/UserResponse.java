package com.chinmai.splitwise.dto;
public class UserResponse {

    private Long userId;
    private String name;
    private String email;

    public UserResponse() {}

    public UserResponse(Long userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}