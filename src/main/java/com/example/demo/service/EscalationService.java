package com.example.demo.service;

import com.example.demo.model.EscalationLog;
import com.example.demo.model.Issue;
import com.example.demo.repository.EscalationLogRepository;
import com.example.demo.repository.IssueRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class EscalationService {

    private final IssueRepository issueRepository;
    private final EscalationLogRepository escalationLogRepository;
    private final EmailService emailService;

    // Department emails by city
    private final Map<String, String> departmentEmails = Map.of(
        "Mumbai", "mumbai.dept@gov.in",
        "Delhi", "delhi.dept@gov.in",
        "Bangalore", "bangalore.dept@gov.in",
        "Chennai", "chennai.dept@gov.in",
        "Hyderabad", "hyderabad.dept@gov.in"
    );

    private final String adminEmail = "admin@gov.in";

    public EscalationService(IssueRepository issueRepository, 
                           EscalationLogRepository escalationLogRepository,
                           EmailService emailService) {
        this.issueRepository = issueRepository;
        this.escalationLogRepository = escalationLogRepository;
        this.emailService = emailService;
    }

    @Scheduled(fixedRate = 3600000) // Run every 1 hour
    @Transactional
    public void processEscalations() {
        List<Issue> overdueIssues = issueRepository.findOverdueIssues(LocalDateTime.now());
        
        for (Issue issue : overdueIssues) {
            escalateIssue(issue);
        }
    }

    private void escalateIssue(Issue issue) {
        issue.setEscalationLevel(issue.getEscalationLevel() + 1);
        
        EscalationLog log = new EscalationLog();
        log.setIssue(issue);
        
        String recipientEmail;
        String remarks;
        
        if (issue.getEscalationLevel() == 1) {
            log.setFromRole(EscalationLog.Role.CITIZEN);
            log.setToRole(EscalationLog.Role.DEPARTMENT);
            recipientEmail = departmentEmails.getOrDefault(issue.getCity(), "default.dept@gov.in");
            remarks = "Issue escalated to department due to missed deadline";
        } else {
            log.setFromRole(EscalationLog.Role.DEPARTMENT);
            log.setToRole(EscalationLog.Role.ADMIN);
            recipientEmail = adminEmail;
            remarks = "Issue escalated to admin due to continued delay";
        }
        
        log.setRemarks(remarks);
        
        // Save entities
        issueRepository.save(issue);
        escalationLogRepository.save(log);
        
        // Send escalation email
        sendEscalationEmail(issue, recipientEmail);
    }

    private void sendEscalationEmail(Issue issue, String recipientEmail) {
        String subject = String.format("ESCALATION ALERT - Issue #%d - Level %d", 
                                     issue.getId(), issue.getEscalationLevel());
        
        String body = String.format("""
            ESCALATION ALERT
            
            Issue Details:
            - Issue ID: %d
            - Category: %s
            - Description: %s
            - Location: %s, %s
            - Priority: %d
            - Escalation Level: %d
            - Created At: %s
            - Deadline: %s
            
            This issue requires immediate attention due to missed deadline.
            
            Please take necessary action to resolve this issue promptly.
            """,
            issue.getId(),
            issue.getCategory(),
            issue.getDescription(),
            issue.getLocation(),
            issue.getCity(),
            issue.getPriority(),
            issue.getEscalationLevel(),
            issue.getCreatedAt(),
            issue.getDeadline()
        );
        
        try {
            emailService.sendEmail(recipientEmail, subject, body);
        } catch (Exception e) {
            System.err.println("Failed to send escalation email for issue " + issue.getId() + ": " + e.getMessage());
        }
    }
}