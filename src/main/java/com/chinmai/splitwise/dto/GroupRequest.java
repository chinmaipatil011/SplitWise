package com.chinmai.splitwise.dto;

import jakarta.validation.constraints.NotBlank;

public class GroupRequest {

    @NotBlank(message = "Group name is required")
    private String name;

    public GroupRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}