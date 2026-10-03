package com.chinmai.splitwise.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.chinmai.splitwise.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByGroup_GroupId(Long groupId);
}
