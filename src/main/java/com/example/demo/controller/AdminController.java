package com.example.demo.controller;

import com.example.demo.service.IssueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final IssueService issueService;
    
    public AdminController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping("/escalate/{id}")
    public ResponseEntity<String> escalateIssue(@PathVariable Long id) {
        issueService.escalateIssue(id);
        return ResponseEntity.ok("Issue escalated successfully!");
    }
}