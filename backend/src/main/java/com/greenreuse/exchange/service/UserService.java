package com.greenreuse.exchange.service;

import com.greenreuse.exchange.dto.AuthRequest;
import com.greenreuse.exchange.dto.AuthResponse;
import com.greenreuse.exchange.dto.ItemDto;
import com.greenreuse.exchange.dto.SignupRequest;
import com.greenreuse.exchange.dto.UserDto;
import com.greenreuse.exchange.dto.UserDashboardDto;
import com.greenreuse.exchange.model.User;
import com.greenreuse.exchange.repository.UserRepository;
import com.greenreuse.exchange.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${app.base-url:http://localhost:5173}")
    private String appBaseUrl;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public AuthResponse signup(SignupRequest request) {
        User user;
        boolean isNew = false;
        if (userRepository.existsByEmail(request.getEmail())) {
            // If user already exists, return their info and token
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found after existsByEmail"));
        } else {
            user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setLocation(request.getLocation());
            user.setPhone(request.getPhone());
            user.setAddress(request.getAddress());
            user.setRole(User.Role.USER);
            user.setSuspended(false);
            user = userRepository.save(user);
            isNew = true;
        }

        // Generate JWT token for the user
        String token = jwtUtil.generateToken(user);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setMessage(isNew ? "Registration successful!" : "Account already exists. Logging you in.");
        response.setUser(UserDto.fromUser(user));
        return response;
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (Boolean.TRUE.equals(user.getSuspended())) {
            AuthResponse response = new AuthResponse();
            response.setToken(null);
            response.setUser(UserDto.fromUser(user));
            String reason = user.getSuspensionReason() != null ? user.getSuspensionReason() : "No reason provided.";
            response.setMessage("Your account is suspended. Reason: " + reason);
            return response;
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setMessage("Login successful");
        response.setUser(UserDto.fromUser(user));
        return response;
    }

    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UserDto.fromUser(user);
    }

    public UserDashboardDto getUserDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<ItemDto> postedItems = user.getItems().stream()
                .map(ItemDto::fromItem)
                .collect(Collectors.toList());

        List<ItemDto> claimedItems = user.getTransactions().stream()
                .map(transaction -> ItemDto.fromItem(transaction.getItem()))
                .collect(Collectors.toList());

        return UserDashboardDto.create(UserDto.fromUser(user), postedItems, claimedItems);
    }

    public List<ItemDto> getUserPostedItems(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user.getItems().stream()
                .map(ItemDto::fromItem)
                .collect(Collectors.toList());
    }

    public List<ItemDto> getUserClaimedItems(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user.getTransactions().stream()
                .map(transaction -> ItemDto.fromItem(transaction.getItem()))
                .collect(Collectors.toList());
    }

    // Admin user management
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
    }

    public void suspendUser(Long id, String reason) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        u.setSuspended(true);
        u.setSuspensionReason(reason);
        userRepository.save(u);
    }

    public void unsuspendUser(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        u.setSuspended(false);
        u.setSuspensionReason(null);
        userRepository.save(u);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
