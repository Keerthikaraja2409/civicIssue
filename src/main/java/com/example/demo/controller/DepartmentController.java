package com.example.demo.controller;

import com.example.demo.model.Issue;
import com.example.demo.repository.IssueRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    private final IssueRepository issueRepo;
    
    public DepartmentController(IssueRepository issueRepo) {
        this.issueRepo = issueRepo;
    }

    @GetMapping("/issues")
    public ResponseEntity<List<Issue>> getIssues() {
        return ResponseEntity.ok(issueRepo.findAll());
    }

    @PutMapping("/resolve/{id}")
    public ResponseEntity<Issue> resolveIssue(@PathVariable Long id) {
        return issueRepo.findById(id)
            .map(issue -> {
                issue.setStatus(Issue.Status.RESOLVED);
                return ResponseEntity.ok(issueRepo.save(issue));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
