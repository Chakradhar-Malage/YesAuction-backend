package com.Chakradhar.YesAuction.controller;

import com.Chakradhar.YesAuction.dto.*;
import com.Chakradhar.YesAuction.entity.Auction;
import com.Chakradhar.YesAuction.entity.Bid;
import com.Chakradhar.YesAuction.entity.User;
import com.Chakradhar.YesAuction.service.AuctionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final SimpMessagingTemplate messagingTemplate;
    public AuctionController(AuctionService auctionService,  SimpMessagingTemplate messagingTemplate) {
        this.auctionService = auctionService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    public ResponseEntity<Auction> createAuction(
            @Valid @RequestBody CreateAuctionRequest request,
            @AuthenticationPrincipal User seller) {  // User from SecurityContext
        Auction auction = auctionService.createAuction(request, seller);
        return ResponseEntity.ok(auction);
    }

    @GetMapping
    public ResponseEntity<List<Auction>> getActiveAuctions() {
        return ResponseEntity.ok(auctionService.getActiveAuctions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuction(@PathVariable Long id) {
        return ResponseEntity.ok(auctionService.getAuctionById(id));
    }

    @PostMapping("/{id}/bid")
    public ResponseEntity<String> placeBid(
            @PathVariable Long id,
            @Valid @RequestBody PlaceBidRequest request,
            @AuthenticationPrincipal User bidder) {
        // Instead of direct processing, send to queue
        auctionService.queueBid(id, request.getAmount(), bidder.getId());
        return ResponseEntity.ok("Bid received and being processed...");
    }
 // in AuctionController or new TestController
    @GetMapping("/test-broadcast/{auctionId}")
    public void testBroadcast(@PathVariable Long auctionId) {
        messagingTemplate.convertAndSend("/topic/auction/" + auctionId,
                new BidUpdateDto(BigDecimal.valueOf(999), "test-broadcaster", LocalDateTime.now()));
    }
}