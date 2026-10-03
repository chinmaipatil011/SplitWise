package com.chinmai.splitwise.dto;

public class GroupResponse {

    private Long groupId;
    private String name;
    private String createdByName;

    public GroupResponse(Long groupId, String name, String createdByName) {
        this.groupId = groupId;
        this.name = name;
        this.createdByName = createdByName;
    }

    public Long getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getCreatedByName() {
        return createdByName;
    }
}