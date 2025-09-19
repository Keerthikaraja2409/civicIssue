package com.example.demo.repository;

import com.example.demo.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    
    @Query("SELECT i FROM Issue i WHERE i.status != 'RESOLVED' AND i.deadline < :currentTime")
    List<Issue> findOverdueIssues(@Param("currentTime") LocalDateTime currentTime);
}
