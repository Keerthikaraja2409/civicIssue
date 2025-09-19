package com.example.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class EscalationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @Enumerated(EnumType.STRING)
    private Role fromRole;

    @Enumerated(EnumType.STRING)
    private Role toRole;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime escalatedAt;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    public enum Role {
        CITIZEN, DEPARTMENT, ADMIN
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Role getFromRole() {
        return fromRole;
    }

    public void setFromRole(Role fromRole) {
        this.fromRole = fromRole;
    }

    public Role getToRole() {
        return toRole;
    }

    public void setToRole(Role toRole) {
        this.toRole = toRole;
    }

    public LocalDateTime getEscalatedAt() {
        return escalatedAt;
    }

    public void setEscalatedAt(LocalDateTime escalatedAt) {
        this.escalatedAt = escalatedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}