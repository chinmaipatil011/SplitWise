package com.chinmai.splitwise.controller;

import com.chinmai.splitwise.dto.GroupRequest;
import com.chinmai.splitwise.dto.GroupResponse;
import com.chinmai.splitwise.service.GroupService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    // POST /api/groups?creatorUserId=1
    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @RequestBody GroupRequest request,
            @RequestParam Long creatorUserId) {

        GroupResponse response =
                groupService.createGroup(request, creatorUserId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // POST /api/groups/{groupId}/members/{userId}
    @PostMapping("/{groupId}/members/{userId}")
    public ResponseEntity<String> addMember(
            @PathVariable Long groupId,
            @PathVariable Long userId) {

        groupService.addMember(groupId, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Member added successfully");
    }
}
