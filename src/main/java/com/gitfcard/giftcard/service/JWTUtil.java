package com.gitfcard.giftcard.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JWTUtil {

    private final JWTService jwtService;

    public JWTUtil(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    public Long extractUserIdFromToken(String token) {
        return jwtService.extractUserId(token);
    }

    public String extractUsernameFromToken(String token) {
        return jwtService.extractUsername(token);
    }

    public Long getCurrentUserIdFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            return extractUserIdFromToken(token);
        }
        return null;
    }

    public boolean isAdmin(String token) {
        Claims claims = jwtService.extractAllClaims(token);
        if (claims == null) return false;

        Object rolesObject = claims.get("roles");
        if (rolesObject instanceof List<?>) {
            List<?> roles = (List<?>) rolesObject;
            return roles.stream()
                        .filter(role -> role instanceof String)
                        .map(role -> (String) role)
                        .anyMatch(role -> role.equalsIgnoreCase("ROLE_ADMIN"));
        }
        return false;
    }
} 
