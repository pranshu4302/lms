package com.lms.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "leave_balance")
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int casualTotal = 10;
    private int casualUsed = 0;

    private int sickTotal = 8;
    private int sickUsed = 0;

    private int annualTotal = 15;
    private int annualUsed = 0;
}