package com.Chakradhar.YesAuction.controller;

import com.Chakradhar.YesAuction.dto.*;
import com.Chakradhar.YesAuction.entity.Auction;
import com.Chakradhar.YesAuction.entity.Bid;
import com.Chakradhar.YesAuction.entity.User;
import com.Chakradhar.YesAuction.service.AuctionService;
import com.Chakradhar.YesAuction.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    public AuctionController(
            AuctionService auctionService,
            SimpMessagingTemplate messagingTemplate,
            UserService userService) {
        this.auctionService = auctionService;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    // CREATE AUCTION
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<Auction> createAuction(
            @Valid @RequestBody CreateAuctionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User seller = userService.findByUsername(userDetails.getUsername());

        Auction auction = auctionService.createAuction(request, seller);

        return ResponseEntity.ok(auction);
    }
    
    @GetMapping
    public ResponseEntity<List<AuctionResponse>> getActiveAuctions() {

        return ResponseEntity.ok(
            auctionService.getActiveAuctionsDto()
        );
    }
    // GET ONE
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponse> getAuction(@PathVariable Long id) {

        AuctionResponse dto =
            auctionService.getAuctionByIdDto(id);

        return ResponseEntity.ok(dto);
    }

    // BID
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/bid")
    public ResponseEntity<String> placeBid(
            @PathVariable Long id,
            @Valid @RequestBody PlaceBidRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User bidder = userService.findByUsername(userDetails.getUsername());

        auctionService.queueBid(id, request.getAmount(), bidder.getId());

        return ResponseEntity.ok("Bid received and being processed...");
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/bids")
    public ResponseEntity<List<BidResponse>> getBidHistory(@PathVariable Long id){
    	List<Bid> bids = auctionService.getBidHistory(id);
    	List<BidResponse> response = bids.stream()
    									.map(bid -> new BidResponse (
    											bid.getId(),
    											bid.getAmount(),
    											bid.getBidder().getUsername(),
    											bid.getBidTime()
    											))
    									.collect(Collectors.toList());
    	return ResponseEntity.ok(response);
    }

    // TEST
    @GetMapping("/test-broadcast/{auctionId}")
    public void testBroadcast(@PathVariable Long auctionId) {
        messagingTemplate.convertAndSend("/topic/auction/" + auctionId,
                new BidUpdateDto(
                        BigDecimal.valueOf(999),
                        "test-broadcaster",
                        LocalDateTime.now()));
    }
}