package com.chinmai.splitwise.service;

import com.chinmai.splitwise.dto.BalanceResponse;
import com.chinmai.splitwise.entity.Expense;
import com.chinmai.splitwise.entity.ExpenseSplit;
import com.chinmai.splitwise.entity.GroupMembership;
import com.chinmai.splitwise.repository.ExpenseRepository;
import com.chinmai.splitwise.repository.ExpenseSplitRepository;
import com.chinmai.splitwise.repository.GroupMembershipRepository;
import com.chinmai.splitwise.repository.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BalanceService {

    private final GroupRepository groupRepository;
    private final GroupMembershipRepository membershipRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository splitRepository;

    public BalanceService(
            GroupRepository groupRepository,
            GroupMembershipRepository membershipRepository,
            ExpenseRepository expenseRepository,
            ExpenseSplitRepository splitRepository) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.expenseRepository = expenseRepository;
        this.splitRepository = splitRepository;
    }

    @Transactional(readOnly = true)
    public List<BalanceResponse> getBalances(Long groupId) {

        if (!groupRepository.existsById(groupId)) {
            throw new RuntimeException("Group not found");
        }

        Map<Long, BigDecimal> balances = new LinkedHashMap<>();
        Map<Long, String> names = new LinkedHashMap<>();

        List<GroupMembership> members =
                membershipRepository.findAllByGroup_GroupId(groupId);

        for (GroupMembership membership : members) {
            var user = membership.getUser();
            balances.put(user.getId(), BigDecimal.ZERO);
            names.put(user.getId(), user.getName());
        }

        List<Expense> expenses =
                expenseRepository.findAllByGroup_GroupId(groupId);

        for (Expense expense : expenses) {
            Long payerId = expense.getPaidBy().getId();
            balances.merge(payerId, expense.getAmount(), BigDecimal::add);
        }

        List<ExpenseSplit> splits =
                splitRepository.findAllByExpense_Group_GroupId(groupId);

        for (ExpenseSplit split : splits) {
            Long userId = split.getUser().getId();
            balances.merge(userId, split.getOwedAmount().negate(),
                    BigDecimal::add);
        }

        List<BalanceResponse> responses = new ArrayList<>();

        for (Map.Entry<Long, BigDecimal> entry : balances.entrySet()) {
            responses.add(new BalanceResponse(
                    names.get(entry.getKey()),
                    entry.getValue()
            ));
        }

        return responses;
    }
}