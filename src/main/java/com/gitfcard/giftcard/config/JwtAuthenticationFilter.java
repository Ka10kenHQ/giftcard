package com.gitfcard.giftcard.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gitfcard.giftcard.service.CustomUserDetailService;
import com.gitfcard.giftcard.service.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JWTService jwtService;
    private final CustomUserDetailService userDetailsService;


    public JwtAuthenticationFilter(JWTService jwtService, CustomUserDetailService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();
        
        logger.debug("Processing request to: {}", requestURI);
        logger.debug("Authorization header: {}", authHeader != null ? "Present" : "Missing");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No Bearer token found, continuing without authentication");
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        logger.debug("JWT token extracted, length: {}", jwt.length());
        
        try {
            final String userEmail = jwtService.extractUsername(jwt);
            logger.debug("Extracted username from JWT: {}", userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.debug("Loading user details for: {}", userEmail);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                logger.debug("User details loaded, authorities: {}", userDetails.getAuthorities());
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    logger.debug("JWT token is valid, setting authentication");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authentication set successfully for user: {}", userEmail);
                } else {
                    logger.warn("JWT token validation failed for user: {}", userEmail);
                }
            } else {
                if (userEmail == null) {
                    logger.warn("Could not extract username from JWT token");
                } else {
                    logger.debug("Authentication already exists in SecurityContext");
                }
            }
        } catch (Exception e) {
            logger.error("JWT authentication failed: {}", e.getMessage(), e);
        }
        
        filterChain.doFilter(request, response);
    }
}

