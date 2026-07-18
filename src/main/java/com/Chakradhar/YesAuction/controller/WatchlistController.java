package com.Chakradhar.YesAuction.controller;

import com.Chakradhar.YesAuction.dto.WatchlistResponse;
import com.Chakradhar.YesAuction.security.JwtUtil;
import com.Chakradhar.YesAuction.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final JwtUtil jwtUtil;

    private Long getUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }

    @PostMapping("/{auctionId}")
    public ResponseEntity<String> addToWatchlist(
            @PathVariable Long auctionId,
            @RequestHeader("Authorization") String token) {
        
        Long userId = getUserId(token);
        if (userId == null) return ResponseEntity.badRequest().build();

        watchlistService.addtoWatchlist(userId, auctionId);
        return ResponseEntity.ok("Added to watchlist");
    }

    @DeleteMapping("/{auctionId}")
    public ResponseEntity<String> removeFromWatchlist(
            @PathVariable Long auctionId,
            @RequestHeader("Authorization") String token) {
        
        Long userId = getUserId(token);
        if (userId == null) return ResponseEntity.badRequest().build();

        watchlistService.removeFromWatchlist(userId, auctionId);
        return ResponseEntity.ok("Removed from watchlist");
    }

    @GetMapping
    public ResponseEntity<List<WatchlistResponse>> getWatchlist(
            @RequestHeader("Authorization") String token) {
        
        Long userId = getUserId(token);
        if (userId == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(watchlistService.getUserWatchlist(userId));
    }

    @GetMapping("/{auctionId}/status")
    public ResponseEntity<Boolean> isInWatchlist(
            @PathVariable Long auctionId,
            @RequestHeader("Authorization") String token) {
        
        Long userId = getUserId(token);
        if (userId == null) return ResponseEntity.badRequest().build();

        boolean isWatched = watchlistService.isInWatchlist(userId, auctionId);
        return ResponseEntity.ok(isWatched);
    }
}