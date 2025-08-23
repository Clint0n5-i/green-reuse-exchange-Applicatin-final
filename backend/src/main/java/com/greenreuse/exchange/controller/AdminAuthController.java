package com.greenreuse.exchange.controller;

import com.greenreuse.exchange.dto.AuthRequest;
import com.greenreuse.exchange.dto.AuthResponse;
import com.greenreuse.exchange.model.Admin;
import com.greenreuse.exchange.repository.AdminRepository;
import com.greenreuse.exchange.service.AdminService;

import jakarta.validation.Valid;

import com.greenreuse.exchange.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:3000")
public class AdminAuthController {
    private final AdminService adminService;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            Admin admin = (Admin) adminService.loadUserByUsername(request.getEmail());
            if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }
            // Generate JWT token for admin
            String token = jwtUtil.generateToken(admin);
            AuthResponse response = new AuthResponse();
            response.setUser(null); // Optionally create and return an AdminDto
            response.setToken(token);
            response.setMessage("Login successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setToken(null);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setUser(null);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
