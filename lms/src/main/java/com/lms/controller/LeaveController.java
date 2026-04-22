package com.lms.controller;

import com.lms.dto.LeaveApplyRequest;
import com.lms.model.LeaveBalance;
import com.lms.model.LeaveRequest;
import com.lms.service.LeaveService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping("/apply")
    public LeaveRequest applyLeave(@RequestBody LeaveApplyRequest request) {
        return leaveService.applyLeave(request);
    }

    @GetMapping("/my")
    public List<LeaveRequest> getMyLeaves(@RequestParam String username) {
        return leaveService.getMyLeaves(username);
    }

    @GetMapping("/pending")
    public List<LeaveRequest> getPendingLeaves() {
        return leaveService.getPendingLeaves();
    }

    @PostMapping("/review/{id}")
    public LeaveRequest reviewLeave(@PathVariable Long id,
            @RequestParam String action,
            @RequestParam(required = false) String remarks) {
        return leaveService.reviewLeave(id, action, remarks);
    }

    @GetMapping("/balance")
    public LeaveBalance getBalance(@RequestParam String username) {
        return leaveService.getBalance(username);
    }
}