package com.Chakradhar.YesAuction.controller;

import com.Chakradhar.YesAuction.dto.*;
import com.Chakradhar.YesAuction.service.*;
import com.Chakradhar.YesAuction.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @RequestHeader("Authorization") String token) {
        
        String jwt = token.substring(7); // Remove "Bearer "
        Long userId = jwtUtil.extractUserId(jwt);
        
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @RequestHeader("Authorization") String token) {
        
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);
        
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);
        
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(
            @RequestHeader("Authorization") String token) {
        
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);
        
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}