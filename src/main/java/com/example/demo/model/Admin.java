package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 30)
    private String username;
    
    @NotBlank
    @Email
    @Column(unique = true)
    private String email;
    
    @JsonIgnore
    @NotBlank
    @Size(min = 6)
    private String password;
}
