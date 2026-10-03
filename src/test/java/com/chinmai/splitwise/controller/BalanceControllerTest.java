package com.chinmai.splitwise.controller;

import com.chinmai.splitwise.service.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BalanceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BalanceService balanceService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new BalanceController(balanceService)).build();
    }

    @Test
    public void getBalances_Returns200() throws Exception {
        mockMvc.perform(get("/api/groups/1/balances"))
                .andExpect(status().isOk());
    }
}
