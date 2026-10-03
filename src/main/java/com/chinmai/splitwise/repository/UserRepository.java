package com.chinmai.splitwise.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chinmai.splitwise.entity.User;

public interface UserRepository extends JpaRepository<User,Long>{
    Optional<User> findByEmail(String email);

}
