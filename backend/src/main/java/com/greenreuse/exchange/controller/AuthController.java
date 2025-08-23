package com.greenreuse.exchange.controller;

import com.greenreuse.exchange.dto.AuthRequest;
import com.greenreuse.exchange.dto.AuthResponse;
import com.greenreuse.exchange.dto.SignupRequest;
import com.greenreuse.exchange.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://your-frontend-domain.com")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        try {
            AuthResponse response = userService.signup(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setToken(null);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setUser(null);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = userService.login(request);
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
