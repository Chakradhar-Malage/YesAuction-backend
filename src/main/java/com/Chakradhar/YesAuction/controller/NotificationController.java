package com.Chakradhar.YesAuction.controller;

import com.Chakradhar.YesAuction.dto.*;
import com.Chakradhar.YesAuction.entity.Notification;
import com.Chakradhar.YesAuction.entity.NotificationType;
import com.Chakradhar.YesAuction.service.*;
import com.Chakradhar.YesAuction.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.Chakradhar.YesAuction.repository.*;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository ;
    private final JwtUtil jwtUtil;

    private Long getUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
    
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @RequestHeader("Authorization") String token) {
        
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @RequestHeader("Authorization") String token) {
        
        String jwt = token.substring(7);
        Long userId = getUserIdFromToken(token);
        
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
        Long userId = getUserIdFromToken(token);
        
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
        Long userId = getUserIdFromToken(token);
        
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
    
    
    @PostMapping("/test")
    public ResponseEntity<String> testNotification(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = getUserIdFromToken(token);
        
        if (userId == null) {
            return ResponseEntity.badRequest().body("Could not extract userId");
        }

        // Create a real notification
        Notification notification = new Notification();
        notification.setUser(userRepository.findById(userId).orElseThrow()); // inject UserRepository
        notification.setTitle("Test Notification");
        notification.setMessage("This is a test notification from the test endpoint");
        notification.setType(NotificationType.SYSTEM);
        notification.setLink("/auctions/17");

        notificationRepository.save(notification); // inject NotificationRepository

        System.out.println("✅ Manual test notification created for userId: " + userId);
        return ResponseEntity.ok("Test notification created successfully!");
    }
}