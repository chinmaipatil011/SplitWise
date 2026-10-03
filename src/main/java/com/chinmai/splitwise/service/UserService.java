package com.chinmai.splitwise.service;

import com.chinmai.splitwise.dto.UserRequest;
import com.chinmai.splitwise.dto.UserResponse;
import com.chinmai.splitwise.entity.User;
import com.chinmai.splitwise.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.chinmai.splitwise.exception.ResourceNotFoundException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(
            passwordEncoder.encode(request.getPassword())
        );

        User savedUser = userRepository.save(user);

        return new UserResponse(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail()
        );
    }
}