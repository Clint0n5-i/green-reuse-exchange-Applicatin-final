package com.greenreuse.exchange.controller;

import com.greenreuse.exchange.dto.ItemDto;
import com.greenreuse.exchange.dto.UserDashboardDto;
import com.greenreuse.exchange.dto.UserDto;
import com.greenreuse.exchange.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "https://your-frontend-domain.com")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            UserDto user = userService.getCurrentUser(userEmail);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<UserDashboardDto> getUserDashboard(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            UserDashboardDto dashboard = userService.getUserDashboard(userEmail);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/posted-items")
    public ResponseEntity<List<ItemDto>> getUserPostedItems(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            List<ItemDto> items = userService.getUserPostedItems(userEmail);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/claimed-items")
    public ResponseEntity<List<ItemDto>> getUserClaimedItems(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            List<ItemDto> items = userService.getUserClaimedItems(userEmail);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/items")
    public ResponseEntity<List<ItemDto>> getUserItems(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            List<ItemDto> items = userService.getUserPostedItems(userEmail);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
