package com.greenreuse.exchange.config;

import com.greenreuse.exchange.model.Admin;
import com.greenreuse.exchange.model.User;
import com.greenreuse.exchange.repository.AdminRepository;
import com.greenreuse.exchange.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AdminMigrationConfig {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    // Migration disabled: No ADMIN role in User entity anymore
    // If you need to migrate legacy admins, do it manually or adjust logic here
}
