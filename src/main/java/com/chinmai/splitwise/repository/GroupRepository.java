package com.chinmai.splitwise.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chinmai.splitwise.entity.Group;



public interface GroupRepository extends JpaRepository<Group, Long> {
}