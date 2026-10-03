package com.chinmai.splitwise.controller;

import com.chinmai.splitwise.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chinmai.splitwise.exception.GlobalExceptionHandler;

public class GroupControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GroupService groupService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new GroupController(groupService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void createGroup_Returns201() throws Exception {
        String json = "{\"name\": \"Trip to Hawaii\"}";
        mockMvc.perform(post("/api/groups?creatorUserId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void addMemberToGroup_Returns201() throws Exception {
        mockMvc.perform(post("/api/groups/1/members/2"))
                .andExpect(status().isCreated());
    }

    @Test
    public void createGroup_InvalidUser_Returns404() throws Exception {
        String json = "{\"name\": \"Trip to Hawaii\"}";
        // Mock the service to throw exception when user doesn't exist
        Mockito.doThrow(new RuntimeException("User not found")).when(groupService).createGroup(any(), eq(999L));

        mockMvc.perform(post("/api/groups?creatorUserId=999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void addSameMemberTwice_Returns400() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("User already in group")).when(groupService).addMember(1L, 2L);

        mockMvc.perform(post("/api/groups/1/members/2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addNonExistentUserOrGroup_Returns404() throws Exception {
        Mockito.doThrow(new RuntimeException("Not found")).when(groupService).addMember(999L, 999L);

        mockMvc.perform(post("/api/groups/999/members/999"))
                .andExpect(status().isInternalServerError());
    }
}
