package com.lms.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String leaveType; // CASUAL, SICK, ANNUAL

    private LocalDate startDate;
    private LocalDate endDate;

    private int totalDays;

    private String reason;

    private String status; // PENDING, APPROVED, REJECTED

    private String remarks;
}