package com.lms.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveApplyRequest {

    private String username;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
}