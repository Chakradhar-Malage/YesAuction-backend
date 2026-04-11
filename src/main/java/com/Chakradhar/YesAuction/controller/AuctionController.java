package com.Chakradhar.YesAuction.controller;

import com.Chakradhar.YesAuction.dto.*;
import com.Chakradhar.YesAuction.entity.Auction;
import com.Chakradhar.YesAuction.entity.AuctionCategory;
import com.Chakradhar.YesAuction.entity.Bid;
import com.Chakradhar.YesAuction.entity.User;
import com.Chakradhar.YesAuction.service.AuctionService;
import com.Chakradhar.YesAuction.service.UserService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Auction> createAuction(
            @ModelAttribute @Valid @RequestBody CreateAuctionRequest request,
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
    
    //get all auction irrespective of status or anything else
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

    
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Auction> updateAuction(
    			@PathVariable Long id,
    			@ModelAttribute @Valid @RequestBody UpdateAuctionRequest request,
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
    
    
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> cancelAuction(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        String message = auctionService.cancelAuction(id, currentUser);
        return ResponseEntity.ok(message);
    }
    
    
    @GetMapping("/{id}/winner")
    public ResponseEntity<AuctionEndResponse> getWinner(@PathVariable Long id) {
        return ResponseEntity.ok(auctionService.getWinner(id));
    }
    
    
    @GetMapping("/search")
    public ResponseEntity<Page<AuctionSearchResponse>> searchAuctions(
    			@RequestParam(required = false) String keyword,
    			@RequestParam(defaultValue = "0") int page,
    			@RequestParam(defaultValue = "10") int size){
    	
    	Pageable pageable = PageRequest.of(	page, size, Sort.by("endTime").descending());
    	Page<AuctionSearchResponse> result = auctionService.searchAuctions(keyword, pageable);
    	return ResponseEntity.ok(result);
    }
    
    //getting auction based on category
    // THIS ONE IS NOT FORCED AUTHENTICATED YET
    @GetMapping(params="category")
    public ResponseEntity<Page<AuctionSearchResponse>> getAuctions(
            @RequestParam(required = false) AuctionCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("endTime").descending());
        Page<AuctionSearchResponse> result = auctionService.getAuctionsByCategory(category, pageable);
        return ResponseEntity.ok(result);
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