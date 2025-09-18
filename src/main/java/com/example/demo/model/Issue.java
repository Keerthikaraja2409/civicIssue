package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;
    
    @NotBlank
    @Size(min = 10, max = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
    
    @NotBlank
    private String location;
    
    private String city;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    private Integer priority = 1;

    @ManyToOne
    @JoinColumn(name = "citizen_id")
    private Citizen citizen;
    
    public enum Category {
        POTHOLE, GARBAGE, STREETLIGHT, WATER, NOISE, OTHER
    }
    
    public enum Status {
        PENDING, IN_PROGRESS, RESOLVED, CLOSED
    }
}
