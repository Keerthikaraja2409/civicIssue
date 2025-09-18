package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.model.Admin;
import com.example.demo.model.Citizen;
import com.example.demo.model.Department;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register/citizen")
    public ResponseEntity<Citizen> registerCitizen(@Valid @RequestBody Citizen citizen) {
        return ResponseEntity.ok(userService.registerCitizen(citizen));
    }

    @PostMapping("/register/department")
    public ResponseEntity<Department> registerDepartment(@Valid @RequestBody Department dept) {
        return ResponseEntity.ok(userService.registerDepartment(dept));
    }

    @PostMapping("/register/admin")
    public ResponseEntity<Admin> registerAdmin(@Valid @RequestBody Admin admin) {
        return ResponseEntity.ok(userService.registerAdmin(admin));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request) {
        Object user = userService.login(request.getEmail(), request.getPassword(), request.getRole());
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.status(401).build();
    }
}
