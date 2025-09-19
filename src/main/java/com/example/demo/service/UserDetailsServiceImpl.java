package com.example.demo.service;

import com.example.demo.model.Admin;
import com.example.demo.model.Citizen;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CitizenRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CitizenRepository citizenRepository;
    private final AdminRepository adminRepository;

    public UserDetailsServiceImpl(CitizenRepository citizenRepository, AdminRepository adminRepository) {
        this.citizenRepository = citizenRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 🔹 First try to load a Citizen
        return citizenRepository.findByEmail(email)
                .map(citizen -> User.builder()
                        .username(citizen.getEmail())
                        .password(citizen.getPassword())
                        .authorities("ROLE_CITIZEN")
                        .build())
                // 🔹 If not found, try to load an Admin
                .orElseGet(() -> adminRepository.findByEmail(email)
                        .map(admin -> User.builder()
                                .username(admin.getEmail())
                                .password(admin.getPassword())
                                .authorities("ROLE_ADMIN")
                                .build())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email))
                );
    }
}
