package com.example.demo.repository;

import com.example.demo.model.EscalationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EscalationLogRepository extends JpaRepository<EscalationLog, Long> {
}