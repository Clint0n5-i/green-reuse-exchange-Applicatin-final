package com.greenreuse.exchange.security;

import com.greenreuse.exchange.service.UserService;
import com.greenreuse.exchange.service.AdminService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final @Lazy UserService userService;
    private final @Lazy AdminService adminService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip JWT processing for public auth endpoints
        String servletPath = request.getServletPath();
        if (servletPath != null && servletPath.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.error("Error extracting username from JWT", e);
            }
        }

        try {
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = null;
                try {
                    userDetails = userService.loadUserByUsername(username);
                } catch (UsernameNotFoundException e) {
                    // Try admin if not found as user
                    try {
                        userDetails = adminService.loadUserByUsername(username);
                    } catch (UsernameNotFoundException ex) {
                        logger.error("User not found as user or admin: {}");
                        throw ex;
                    }
                }

                boolean valid = false;
                try {
                    valid = jwtUtil.validateToken(jwt, userDetails);
                } catch (Exception ex) {
                    logger.warn("Invalid JWT token: {}", ex);
                }

                if (valid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("JWT filter processing error", e);
            // Do not block the request; continue unauthenticated so public endpoints still
            // work
        }

        filterChain.doFilter(request, response);
    }
}
