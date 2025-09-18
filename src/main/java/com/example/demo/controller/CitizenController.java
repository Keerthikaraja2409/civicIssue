package com.example.demo.controller;

import com.example.demo.model.Issue;
import com.example.demo.service.IssueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/citizen")
public class CitizenController {

    private final IssueService issueService;
    
    public CitizenController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping("/report")
    public ResponseEntity<Issue> reportIssue(@Valid @RequestBody Issue issue) {
        Issue createdIssue = issueService.createIssue(issue);
        return ResponseEntity.ok(createdIssue);
    }
}