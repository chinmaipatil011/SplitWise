package com.chinmai.splitwise.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chinmai.splitwise.entity.Settlement;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
}