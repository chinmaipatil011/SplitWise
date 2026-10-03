
package com.chinmai.splitwise.service;
import java.math.RoundingMode;
import com.chinmai.splitwise.dto.ExpenseUpdateRequest;
import com.chinmai.splitwise.dto.ExpenseRequest;
import com.chinmai.splitwise.dto.ExpenseResponse;
import com.chinmai.splitwise.entity.Expense;
import com.chinmai.splitwise.entity.ExpenseSplit;
import com.chinmai.splitwise.entity.Group;
import com.chinmai.splitwise.entity.User;
import com.chinmai.splitwise.repository.ExpenseRepository;
import com.chinmai.splitwise.repository.ExpenseSplitRepository;
import com.chinmai.splitwise.repository.GroupMembershipRepository;
import com.chinmai.splitwise.repository.GroupRepository;
import com.chinmai.splitwise.repository.UserRepository;
import com.chinmai.splitwise.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMembershipRepository groupMembershipRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            ExpenseSplitRepository expenseSplitRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            GroupMembershipRepository groupMembershipRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMembershipRepository = groupMembershipRepository;
    }

    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request) {

        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        User paidBy = userRepository.findById(request.getPaidByUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Payer not found"));

        if (!groupMembershipRepository.existsByGroup_GroupIdAndUser_Id(
                group.getGroupId(), paidBy.getId())) {
            throw new IllegalArgumentException("Payer is not a member of this group");
        }

        List<Long> memberIds = request.getSplitBetweenUserIds();

        if (memberIds == null || memberIds.isEmpty()) {
            throw new IllegalArgumentException("At least one member is required for the split");
        }

        if (new HashSet<>(memberIds).size() != memberIds.size()) {
            throw new IllegalArgumentException("Duplicate members in split");
        }

        for (Long memberId : memberIds) {
            if (!groupMembershipRepository.existsByGroup_GroupIdAndUser_Id(
                    group.getGroupId(), memberId)) {
                throw new IllegalArgumentException("A split member is not in this group");
            }
        }

        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setPaidBy(paidBy);
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());

        Expense savedExpense = expenseRepository.save(expense);

        BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal baseShare = amount.divide(
                BigDecimal.valueOf(memberIds.size()), 2, RoundingMode.DOWN);

        BigDecimal allocated = BigDecimal.ZERO;

        for (int i = 0; i < memberIds.size(); i++) {
            User member = userRepository.findById(memberIds.get(i))
                    .orElseThrow(() -> new ResourceNotFoundException("Split member not found"));

            BigDecimal share;
            if (i == memberIds.size() - 1) {
                share = amount.subtract(allocated);
            } else {
                share = baseShare;
                allocated = allocated.add(share);
            }

            ExpenseSplit split = new ExpenseSplit();
            split.setExpense(savedExpense);
            split.setUser(member);
            split.setOwedAmount(share);

            expenseSplitRepository.save(split);
        }

        return new ExpenseResponse(
                savedExpense.getExpenseId(),
                savedExpense.getDescription(),
                savedExpense.getAmount(),
                paidBy.getName()
        );
    }
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getGroupExpenses(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found");
        }

        List<Expense> expenses =
                expenseRepository.findAllByGroup_GroupId(groupId);

        return expenses.stream()
                .map(expense -> new ExpenseResponse(
                        expense.getExpenseId(),
                        expense.getDescription(),
                        expense.getAmount(),
                        expense.getPaidBy().getName()
                ))
                .toList();
    }
    @Transactional(readOnly = true)
public ExpenseResponse getExpenseById(Long expenseId) {
    Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Expense not found"));

    return new ExpenseResponse(
            expense.getExpenseId(),
            expense.getDescription(),
            expense.getAmount(),
            expense.getPaidBy().getName()
    );
}
@Transactional
public void deleteExpense(Long expenseId) {
    Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Expense not found"));

    List<ExpenseSplit> splits =
            expenseSplitRepository.findAllByExpense_ExpenseId(expenseId);

    expenseSplitRepository.deleteAll(splits);
    expenseRepository.delete(expense);
}

@Transactional
public ExpenseResponse updateExpense(Long expenseId, ExpenseUpdateRequest request) {

    Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

    if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Amount must be greater than zero");
    }

    if (request.getDescription() == null || request.getDescription().isBlank()) {
        throw new IllegalArgumentException("Description cannot be empty");
    }

    List<ExpenseSplit> splits =
            expenseSplitRepository.findAllByExpense_ExpenseId(expenseId);

    if (splits.isEmpty()) {
        throw new IllegalArgumentException("Expense has no splits");
    }

    BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
    BigDecimal share = amount.divide(
            BigDecimal.valueOf(splits.size()), 2, RoundingMode.DOWN);

    BigDecimal allocated = BigDecimal.ZERO;

    for (int i = 0; i < splits.size(); i++) {
        ExpenseSplit split = splits.get(i);

        BigDecimal owedAmount;
        if (i == splits.size() - 1) {
            owedAmount = amount.subtract(allocated);
        } else {
            owedAmount = share;
            allocated = allocated.add(share);
        }

        split.setOwedAmount(owedAmount);
    }

    expense.setAmount(amount);
    expense.setDescription(request.getDescription().trim());

    expenseSplitRepository.saveAll(splits);
    Expense updatedExpense = expenseRepository.save(expense);

    return new ExpenseResponse(
            updatedExpense.getExpenseId(),
            updatedExpense.getDescription(),
            updatedExpense.getAmount(),
            updatedExpense.getPaidBy().getName()
    );
}

    
}

