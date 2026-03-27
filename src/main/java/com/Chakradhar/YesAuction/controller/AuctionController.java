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
    
    //get all auctions irrespective of status or anything else
    @GetMapping("/all")
    public ResponseEntity<List<AuctionResponse>> getAllAuctions() {

        return ResponseEntity.ok(
            auctionService.getAllAuctionsDto()
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
     // Validate synchronously
        auctionService.validateBid(id, request.getAmount());

        //Only valid bids go to queue
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

    
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Auction> updateAuction(
    			@PathVariable Long id,
    			@Valid @RequestBody UpdateAuctionRequest request,
    			@AuthenticationPrincipal User currentUser) {
    	Auction updateAuction = auctionService.updateAuction(id, request, currentUser);
    	return ResponseEntity.ok(updateAuction);
    }
    
    @PostMapping("/{id}/end")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuctionEndResponse> endAuction(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        AuctionEndResponse response = auctionService.endAuction(id, currentUser);
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