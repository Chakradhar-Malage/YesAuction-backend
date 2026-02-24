package com.Chakradhar.YesAuction.service;

import com.Chakradhar.YesAuction.config.RabbitMQConfig;
import com.Chakradhar.YesAuction.dto.AuctionUpdateDto;
import com.Chakradhar.YesAuction.dto.BidMessageDto;
import com.Chakradhar.YesAuction.dto.BidUpdateDto;
import com.Chakradhar.YesAuction.dto.CreateAuctionRequest;
import com.Chakradhar.YesAuction.dto.PlaceBidRequest;
import com.Chakradhar.YesAuction.entity.*;
import com.Chakradhar.YesAuction.repository.AuctionRepository;
import com.Chakradhar.YesAuction.repository.BidRepository;
import com.Chakradhar.YesAuction.repository.ItemRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ItemRepository itemRepository;
    private final BidRepository bidRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RabbitTemplate rabbitTemplate;

    public AuctionService(AuctionRepository auctionRepository, 
    					ItemRepository itemRepository, 
    					BidRepository bidRepository, 
    					SimpMessagingTemplate messagingTemplate,
    					RabbitTemplate rabbitTemplate) {
        this.auctionRepository = auctionRepository;
        this.itemRepository = itemRepository;
        this.bidRepository = bidRepository;
        this.messagingTemplate = messagingTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public Auction createAuction(CreateAuctionRequest request, User seller) {
        Item item = Item.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();
        itemRepository.save(item);

        Auction auction = Auction.builder()
                .item(item)
                .seller(seller)
                .startingPrice(request.getStartingPrice())
                .currentPrice(request.getStartingPrice())
                .startTime(LocalDateTime.now())
                .endTime(request.getEndTime())
                .status(AuctionStatus.ACTIVE)
                .build();

        return auctionRepository.save(auction);
    }

    public List<Auction> getActiveAuctions() {
        return auctionRepository.findByStatus(AuctionStatus.ACTIVE);
    }

    public Auction getAuctionById(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
    }

    @Transactional
    public Bid placeBid(Long auctionId, PlaceBidRequest request, User bidder) {
        Auction auction = getAuctionById(auctionId);

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new RuntimeException("Auction is not active");
        }
        if (LocalDateTime.now().isAfter(auction.getEndTime())) {
            auction.setStatus(AuctionStatus.ENDED);
            auctionRepository.save(auction);
            throw new RuntimeException("Auction has ended");
        }

        BigDecimal minBid = auction.getCurrentPrice().add(BigDecimal.valueOf(1));  // simple increment rule
        if (request.getAmount().compareTo(minBid) < 0) {
            throw new RuntimeException("Bid must be higher than current price + increment");
        }

        Bid bid = Bid.builder()
                .auction(auction)
                .bidder(bidder)
                .amount(request.getAmount())
                .bidTime(LocalDateTime.now())
                .build();

        auction.addBid(bid);
        bidRepository.save(bid);
        auctionRepository.save(auction);  // save updated currentPrice

	    messagingTemplate.convertAndSend(
	    	    "/topic/auction/" + auction.getId(),
	    	    new AuctionUpdateDto(
	    	        auction.getId(),
	    	        auction.getCurrentPrice(),
	    	        new BidUpdateDto(bid.getAmount(), bid.getBidder().getUsername(), bid.getBidTime())
	    	    )
	    	);
    // Helper to convert entity to DTO
	    return bid;
    }
    
    @Transactional
    public void queueBid(Long auctionId, BigDecimal amount, Long bidderId) {
        BidMessageDto message = new BidMessageDto(auctionId, bidderId, amount);
        rabbitTemplate.convertAndSend(RabbitMQConfig.BID_EXCHANGE, RabbitMQConfig.BID_ROUTING_KEY, message);
    }
}
