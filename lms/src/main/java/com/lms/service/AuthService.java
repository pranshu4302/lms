package com.lms.service;

import com.lms.dto.RegisterRequest;
import com.lms.model.User;
import com.lms.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.lms.model.Role;

import com.lms.model.LeaveBalance;
import com.lms.repository.LeaveBalanceRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    public AuthService(UserRepository userRepository, LeaveBalanceRepository leaveBalanceRepository) {
        this.userRepository = userRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
    }

    public User login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    public User register(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        long count = userRepository.count();

        if (count == 0) {
            user.setRole(Role.ROLE_ADMIN); // first user = admin
        } else {
            user.setRole(Role.ROLE_EMPLOYEE);
        }

        User savedUser = userRepository.save(user);

        LeaveBalance balance = new LeaveBalance();
        balance.setUser(savedUser);

        balance.setCasualTotal(10);
        balance.setCasualUsed(0);

        balance.setSickTotal(8);
        balance.setSickUsed(0);

        balance.setAnnualTotal(15);
        balance.setAnnualUsed(0);

        leaveBalanceRepository.save(balance);

        return savedUser;
    }
}