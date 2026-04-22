package com.lms.repository;

import com.lms.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByUserUsername(String username);
    List<LeaveRequest> findByStatus(String status);

}