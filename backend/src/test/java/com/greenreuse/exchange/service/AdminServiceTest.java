package com.greenreuse.exchange.service;

import com.greenreuse.exchange.model.Admin;
import com.greenreuse.exchange.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AdminServiceTest {
    @Mock
    private AdminRepository adminRepository;
    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_found() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        when(adminRepository.findByEmail(any())).thenReturn(Optional.of(admin));
        assertEquals(admin, adminService.loadUserByUsername("admin@example.com"));
    }

    @Test
    void loadUserByUsername_notFound() {
        when(adminRepository.findByEmail(any())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> adminService.loadUserByUsername("notfound@example.com"));
    }
}
