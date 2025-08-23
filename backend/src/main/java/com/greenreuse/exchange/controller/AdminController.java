package com.greenreuse.exchange.controller;

import com.greenreuse.exchange.dto.ItemDto;
import com.greenreuse.exchange.service.ItemService;
import com.greenreuse.exchange.dto.UserDto;
import com.greenreuse.exchange.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://your-frontend-domain.com")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ItemService itemService;
    private final UserService userService;

    @GetMapping("/items")
    public ResponseEntity<List<ItemDto>> getAllItemsForAdmin() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItemAsAdmin(@PathVariable Long id, Authentication authentication) {
        try {
            String adminEmail = authentication.getName();
            itemService.deleteItem(id, adminEmail);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{id}/suspend")
    public ResponseEntity<Void> suspendUser(@PathVariable Long id, @RequestBody(required = false) String reason) {
        if (reason == null || reason.isBlank()) {
            reason = "No reason provided.";
        }
        userService.suspendUser(id, reason);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/unsuspend")
    public ResponseEntity<Void> unsuspendUser(@PathVariable Long id) {
        userService.unsuspendUser(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
