package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String role;  // "CITIZEN" or "ADMIN" or "DEPARTMENT"

    // ---------- Constructors ----------
    public LoginRequest() {
    }

    public LoginRequest(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ---------- Getters & Setters ----------
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // ---------- toString ----------
    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
