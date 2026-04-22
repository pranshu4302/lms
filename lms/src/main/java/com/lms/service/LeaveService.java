package com.lms.service;

import com.lms.dto.LeaveApplyRequest;
import com.lms.model.LeaveBalance;
import com.lms.model.LeaveRequest;
import com.lms.model.User;
import com.lms.repository.LeaveBalanceRepository;
import com.lms.repository.LeaveRequestRepository;
import com.lms.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveService {

    private final LeaveRequestRepository leaveRepo;
    private final UserRepository userRepo;
    private final LeaveBalanceRepository balanceRepo;

    public LeaveService(LeaveRequestRepository leaveRepo,
                        UserRepository userRepo,
                        LeaveBalanceRepository balanceRepo) {
        this.leaveRepo = leaveRepo;
        this.userRepo = userRepo;
        this.balanceRepo = balanceRepo;
    }

    public LeaveRequest applyLeave(LeaveApplyRequest request) {

        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Date validation
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }

        int days = (int) (request.getEndDate().toEpochDay() - request.getStartDate().toEpochDay()) + 1;

        // ✅ Days validation
        if (days <= 0) {
            throw new RuntimeException("Invalid number of days");
        }

        LeaveBalance balance = balanceRepo
                .findByUserUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Balance not found"));

        // ✅ Balance + leave type validation
        switch (request.getLeaveType()) {
            case "CASUAL" -> {
                int remaining = balance.getCasualTotal() - balance.getCasualUsed();
                if (days > remaining) {
                    throw new RuntimeException("Not enough casual leaves");
                }
            }
            case "SICK" -> {
                int remaining = balance.getSickTotal() - balance.getSickUsed();
                if (days > remaining) {
                    throw new RuntimeException("Not enough sick leaves");
                }
            }
            case "ANNUAL" -> {
                int remaining = balance.getAnnualTotal() - balance.getAnnualUsed();
                if (days > remaining) {
                    throw new RuntimeException("Not enough annual leaves");
                }
            }
            default -> throw new RuntimeException("Invalid leave type");
        }

        LeaveRequest leave = new LeaveRequest();
        leave.setUser(user);
        leave.setLeaveType(request.getLeaveType());
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setReason(request.getReason());
        leave.setTotalDays(days);
        leave.setStatus("PENDING");

        return leaveRepo.save(leave);
    }

    public List<LeaveRequest> getMyLeaves(String username) {
        return leaveRepo.findByUserUsername(username);
    }

    public List<LeaveRequest> getPendingLeaves() {
        return leaveRepo.findByStatus("PENDING");
    }

    public LeaveRequest reviewLeave(Long id, String action, String remarks) {

        LeaveRequest leave = leaveRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        if (!leave.getStatus().equals("PENDING")) {
            throw new RuntimeException("Already processed");
        }

        leave.setStatus(action);
        leave.setRemarks(remarks);

        if (action.equals("APPROVED")) {

            LeaveBalance balance = balanceRepo
                    .findByUserUsername(leave.getUser().getUsername())
                    .orElseThrow(() -> new RuntimeException("Balance not found"));

            int days = leave.getTotalDays();

            switch (leave.getLeaveType()) {
                case "CASUAL" -> balance.setCasualUsed(balance.getCasualUsed() + days);
                case "SICK" -> balance.setSickUsed(balance.getSickUsed() + days);
                case "ANNUAL" -> balance.setAnnualUsed(balance.getAnnualUsed() + days);
            }

            balanceRepo.save(balance);
        }

        return leaveRepo.save(leave);
    }

    public LeaveBalance getBalance(String username) {
        return balanceRepo.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Balance not found"));
    }
}