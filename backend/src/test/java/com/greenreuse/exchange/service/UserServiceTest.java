package com.greenreuse.exchange.service;

import com.greenreuse.exchange.dto.AuthRequest;
import com.greenreuse.exchange.dto.AuthResponse;
import com.greenreuse.exchange.dto.SignupRequest;
import com.greenreuse.exchange.dto.UserDto;
import com.greenreuse.exchange.model.User;
import com.greenreuse.exchange.repository.UserRepository;
import com.greenreuse.exchange.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Test
    void signup_shouldThrowExceptionIfUserNotFoundAfterExistsByEmail() {
        SignupRequest request = new SignupRequest();
        request.setEmail("notfound@example.com");
        when(userRepository.existsByEmail(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.signup(request));
    }

    @Test
    void login_shouldThrowExceptionIfUserNotFound() {
        AuthRequest request = new AuthRequest();
        request.setEmail("notfound@example.com");
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> userService.login(request));
    }

    @Test
    void login_shouldThrowExceptionIfPasswordInvalid() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrong");
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encoded");
        user.setSuspended(false);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        assertThrows(org.springframework.security.authentication.BadCredentialsException.class,
                () -> userService.login(request));
    }

    @Test
    void getCurrentUser_shouldThrowExceptionIfUserNotFound() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> userService.getCurrentUser("notfound@example.com"));
    }

    @Test
    void getUserDashboard_shouldThrowExceptionIfUserNotFound() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> userService.getUserDashboard("notfound@example.com"));
    }

    @Test
    void getUserPostedItems_shouldThrowExceptionIfUserNotFound() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> userService.getUserPostedItems("notfound@example.com"));
    }

    @Test
    void getUserClaimedItems_shouldThrowExceptionIfUserNotFound() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> userService.getUserClaimedItems("notfound@example.com"));
    }

    @Test
    void suspendUser_shouldThrowExceptionIfUserNotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> userService.suspendUser(1L, "reason"));
    }

    @Test
    void unsuspendUser_shouldThrowExceptionIfUserNotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> userService.unsuspendUser(1L));
    }

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signup_newUser_success() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setName("Test User");
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateToken(any())).thenReturn("token");

        AuthResponse response = userService.signup(request);
        assertEquals("Registration successful!", response.getMessage());
        assertEquals("token", response.getToken());
        assertNotNull(response.getUser());
    }

    @Test
    void signup_existingUser_logsIn() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.existsByEmail(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(any())).thenReturn("token");

        AuthResponse response = userService.signup(request);
        assertEquals("Account already exists. Logging you in.", response.getMessage());
        assertEquals("token", response.getToken());
        assertNotNull(response.getUser());
    }

    @Test
    void login_success() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encoded");
        user.setSuspended(false);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn("token");

        AuthResponse response = userService.login(request);
        assertEquals("Login successful", response.getMessage());
        assertEquals("token", response.getToken());
        assertNotNull(response.getUser());
    }

    @Test
    void login_suspendedUser() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encoded");
        user.setSuspended(true);
        user.setSuspensionReason("Violation");
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        AuthResponse response = userService.login(request);
        assertNull(response.getToken());
        assertTrue(response.getMessage().contains("suspended"));
    }
}
