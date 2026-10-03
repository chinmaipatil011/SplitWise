package com.chinmai.splitwise.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.chinmai.splitwise.entity.ExpenseSplit;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {
    List<ExpenseSplit> findAllByExpense_Group_GroupId(Long groupId);
    List<ExpenseSplit> findAllByExpense_ExpenseId(Long expenseId);
}