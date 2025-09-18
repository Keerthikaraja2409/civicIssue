package com.example.demo.service;

import com.example.demo.model.Admin;
import com.example.demo.model.Citizen;
import com.example.demo.model.Department;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CitizenRepository;
import com.example.demo.repository.DepartmentRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final CitizenRepository citizenRepo;
    private final DepartmentRepository deptRepo;
    private final AdminRepository adminRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public UserService(CitizenRepository citizenRepo, DepartmentRepository deptRepo, 
                      AdminRepository adminRepo, BCryptPasswordEncoder passwordEncoder) {
        this.citizenRepo = citizenRepo;
        this.deptRepo = deptRepo;
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public Citizen registerCitizen(Citizen citizen) {
        validateCitizen(citizen);
        citizen.setPassword(passwordEncoder.encode(citizen.getPassword()));
        return citizenRepo.save(citizen);
    }

    public Department registerDepartment(Department dept) {
        validateDepartment(dept);
        return deptRepo.save(dept);
    }

    public Admin registerAdmin(Admin admin) {
        validateAdmin(admin);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepo.save(admin);
    }

    public Object login(String email, String password, String role) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password) || !StringUtils.hasText(role)) {
            return null;
        }
        
        switch (role.toLowerCase(Locale.ROOT)) {
            case "citizen":
                return citizenRepo.findByEmail(email)
                        .filter(c -> passwordEncoder.matches(password, c.getPassword()))
                        .orElse(null);

            case "department":
                return deptRepo.findByEmail(email).orElse(null);

            case "admin":
                return adminRepo.findByEmail(email)
                        .filter(a -> passwordEncoder.matches(password, a.getPassword()))
                        .orElse(null);

            default:
                return null;
        }
    }
    
    private void validateCitizen(Citizen citizen) {
        if (citizen == null) throw new IllegalArgumentException("Citizen cannot be null");
        if (!StringUtils.hasText(citizen.getName())) throw new IllegalArgumentException("Name is required");
        if (!isValidEmail(citizen.getEmail())) throw new IllegalArgumentException("Valid email is required");
        if (!StringUtils.hasText(citizen.getPassword())) throw new IllegalArgumentException("Password is required");
    }
    
    private void validateDepartment(Department dept) {
        if (dept == null) throw new IllegalArgumentException("Department cannot be null");
        if (!StringUtils.hasText(dept.getName())) throw new IllegalArgumentException("Department name is required");
        if (!isValidEmail(dept.getEmail())) throw new IllegalArgumentException("Valid email is required");
        if (!StringUtils.hasText(dept.getPhone())) throw new IllegalArgumentException("Phone is required");
    }
    
    private void validateAdmin(Admin admin) {
        if (admin == null) throw new IllegalArgumentException("Admin cannot be null");
        if (!StringUtils.hasText(admin.getUsername())) throw new IllegalArgumentException("Username is required");
        if (!isValidEmail(admin.getEmail())) throw new IllegalArgumentException("Valid email is required");
        if (!StringUtils.hasText(admin.getPassword())) throw new IllegalArgumentException("Password is required");
    }
    
    private boolean isValidEmail(String email) {
        return StringUtils.hasText(email) && EMAIL_PATTERN.matcher(email).matches();
    }
}
