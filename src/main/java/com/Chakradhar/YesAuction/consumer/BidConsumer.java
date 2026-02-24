package com.Chakradhar.YesAuction.consumer;

import com.Chakradhar.YesAuction.config.RabbitMQConfig;
import com.Chakradhar.YesAuction.dto.AuctionUpdateDto;
import com.Chakradhar.YesAuction.dto.BidMessageDto;
import com.Chakradhar.YesAuction.dto.BidUpdateDto;
import com.Chakradhar.YesAuction.entity.AuctionStatus;
import com.Chakradhar.YesAuction.entity.Bid;
import com.Chakradhar.YesAuction.entity.User;
import com.Chakradhar.YesAuction.repository.AuctionRepository;
import com.Chakradhar.YesAuction.repository.BidRepository;
import com.Chakradhar.YesAuction.repository.UserRepository;
import com.Chakradhar.YesAuction.service.AuctionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class BidConsumer {

    private final AuctionService auctionService;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public BidConsumer(AuctionService auctionService, AuctionRepository auctionRepository, BidRepository bidRepository,
			UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
		super();
		this.auctionService = auctionService;
		this.auctionRepository = auctionRepository;
		this.bidRepository = bidRepository;
		this.userRepository = userRepository;
		this.messagingTemplate = messagingTemplate;
	}


    // constructor injection

    @RabbitListener(queues = RabbitMQConfig.BID_QUEUE)
    public void processBid(BidMessageDto message) {
        try {
            // Re-fetch auction to avoid stale data
            var auction = auctionRepository.findById(message.getAuctionId())
                    .orElseThrow(() -> new RuntimeException("Auction not found"));

            User bidder = userRepository.findById(message.getBidderId())
                    .orElseThrow(() -> new RuntimeException("Bidder not found"));

            // Reuse your existing placeBid logic (extract to private method if needed)
            // For simplicity, duplicate minimal validation & save here
            if (auction.getStatus() != AuctionStatus.ACTIVE || LocalDateTime.now().isAfter(auction.getEndTime())) {
                // Could send rejection message back via another queue
                return;
            }

            BigDecimal minBid = auction.getCurrentPrice().add(BigDecimal.ONE);
            if (message.getAmount().compareTo(minBid) < 0) {
                // Reject - could notify via queue
                return;
            }

            Bid bid = Bid.builder()
                    .auction(auction)
                    .bidder(bidder)
                    .amount(message.getAmount())
                    .bidTime(LocalDateTime.now())
                    .build();

            bidRepository.save(bid);
            auction.addBid(bid);
            auctionRepository.save(auction);

            // Broadcast via WebSocket
            messagingTemplate.convertAndSend(
                    "/topic/auction/" + auction.getId(),
                    new AuctionUpdateDto(
                            auction.getId(),
                            auction.getCurrentPrice(),
                            new BidUpdateDto(bid.getAmount(), bidder.getUsername(), bid.getBidTime())
                    )
            );

            // TODO: Queue notification for outbid users

        } catch (Exception e) {
            // Log error, could reject/requeue message
            System.err.println("Bid processing failed: " + e.getMessage());
        }
    }
}