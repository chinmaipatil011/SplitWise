
package com.chinmai.splitwise.controller;

import com.chinmai.splitwise.dto.BalanceResponse;
import com.chinmai.splitwise.service.BalanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/{groupId}/balances")
    public List<BalanceResponse> getBalances(
            @PathVariable Long groupId) {
        return balanceService.getBalances(groupId);
    }
}
