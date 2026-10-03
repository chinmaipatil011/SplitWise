
package com.chinmai.splitwise.controller;
import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;
import jakarta.validation.Valid;
import com.chinmai.splitwise.dto.ExpenseUpdateRequest;
import com.chinmai.splitwise.dto.ExpenseResponse;
import com.chinmai.splitwise.dto.ExpenseRequest;
import com.chinmai.splitwise.dto.ExpenseResponse;
import com.chinmai.splitwise.service.ExpenseService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody ExpenseRequest request) {

        ExpenseResponse response =
                expenseService.createExpense(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ExpenseResponse>> getGroupExpenses(
        @PathVariable Long groupId) {

    List<ExpenseResponse> expenses =
            expenseService.getGroupExpenses(groupId);

    return ResponseEntity.ok(expenses);
}
@GetMapping("/{expenseId}")
public ResponseEntity<ExpenseResponse> getExpenseById(
        @PathVariable Long expenseId) {

    ExpenseResponse response =
            expenseService.getExpenseById(expenseId);

    return ResponseEntity.ok(response);
}
@DeleteMapping("/{expenseId}")
public ResponseEntity<String> deleteExpense(
        @PathVariable Long expenseId) {

    expenseService.deleteExpense(expenseId);

    return ResponseEntity.ok("Expense deleted successfully");
}
@PutMapping("/{expenseId}")
public ExpenseResponse updateExpense(
        @PathVariable Long expenseId,
        @Valid @RequestBody ExpenseUpdateRequest request) {
    return expenseService.updateExpense(expenseId, request);
}
    
}
