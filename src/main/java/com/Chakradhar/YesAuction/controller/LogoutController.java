package com.Chakradhar.YesAuction.controller;

import com.Chakradhar.YesAuction.entity.User;
import com.Chakradhar.YesAuction.security.JwtUtil;
import com.Chakradhar.YesAuction.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LogoutController {

    private final TokenBlacklistService tokenBlacklistService;
    private final JwtUtil jwtUtil;

    public LogoutController(TokenBlacklistService tokenBlacklistService, JwtUtil jwtUtil) {
        this.tokenBlacklistService = tokenBlacklistService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, 
                                         @AuthenticationPrincipal User currentUser) {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            // Get remaining expiration time from JWT
            long remainingTime = jwtUtil.extractExpiration(token).getTime() - System.currentTimeMillis();
            
            if (remainingTime > 0) {
                tokenBlacklistService.blacklistToken(token, remainingTime);
            }
        }

        return ResponseEntity.ok("Logged out successfully. Token has been invalidated.");
    }
}