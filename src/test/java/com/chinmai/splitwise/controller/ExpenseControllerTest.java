package com.chinmai.splitwise.controller;

import com.chinmai.splitwise.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExpenseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ExpenseService expenseService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new ExpenseController(expenseService)).build();
    }

    @Test
    public void createExpense_Returns201() throws Exception {
        String json = "{\"groupId\": 1, \"paidByUserId\": 1, \"amount\": 100.50, \"description\": \"Dinner\", \"splitBetweenUserIds\": [1,2]}";
        mockMvc.perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void getGroupExpenses_Returns200() throws Exception {
        mockMvc.perform(get("/api/expenses/group/1")).andExpect(status().isOk());
    }

    @Test
    public void getExpenseById_Returns200() throws Exception {
        mockMvc.perform(get("/api/expenses/1")).andExpect(status().isOk());
    }

    @Test
    public void deleteExpense_Returns200() throws Exception {
        mockMvc.perform(delete("/api/expenses/1")).andExpect(status().isOk());
    }

    @Test
    public void updateExpense_Returns200() throws Exception {
        String json = "{\"amount\": 150.00, \"description\": \"Lunch\"}";
        mockMvc.perform(put("/api/expenses/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
    }
}
