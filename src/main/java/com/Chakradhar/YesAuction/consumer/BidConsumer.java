package com.Chakradhar.YesAuction.consumer;

import com.Chakradhar.YesAuction.config.RabbitMQConfig;
import com.Chakradhar.YesAuction.dto.BidMessageDto;
import com.Chakradhar.YesAuction.dto.OutbidNotificationDto;
import com.Chakradhar.YesAuction.entity.AuctionStatus;
import com.Chakradhar.YesAuction.entity.Bid;
import com.Chakradhar.YesAuction.entity.User;
import com.Chakradhar.YesAuction.repository.AuctionRepository;
import com.Chakradhar.YesAuction.repository.BidRepository;
import com.Chakradhar.YesAuction.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class BidConsumer {

    private static final Logger log = LoggerFactory.getLogger(BidConsumer.class);

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public BidConsumer(
            AuctionRepository auctionRepository,
            BidRepository bidRepository,
            UserRepository userRepository,
            RabbitTemplate rabbitTemplate,
            SimpMessagingTemplate messagingTemplate) {

        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.messagingTemplate = messagingTemplate;
    }


    @RabbitListener(queues = RabbitMQConfig.BID_QUEUE)
    @Transactional
    public void processBid(BidMessageDto message) {

        try {
            log.info("Processing bid for auction {}", message.getAuctionId());

            // Fetch auction
            var auction = auctionRepository.findById(message.getAuctionId())
                    .orElseThrow(() -> new RuntimeException("Auction not found"));

            // Fetch bidder
            User bidder = userRepository.findById(message.getBidderId())
                    .orElseThrow(() -> new RuntimeException("Bidder not found"));

            // Validate auction status
            if (auction.getStatus() != AuctionStatus.ACTIVE ||
                LocalDateTime.now().isAfter(auction.getEndTime())) {

                log.warn("Auction {} is not active", auction.getId());
                return;
            }

            // Validate bid amount
            BigDecimal minBid = auction.getCurrentPrice().add(BigDecimal.ONE);

            if (message.getAmount().compareTo(minBid) < 0) {
                log.warn("Bid too low: {}", message.getAmount());
                return;
            }

            // Create bid
            Bid bid = Bid.builder()
            		.messageId(message.getMessageId())
                    .auction(auction)
                    .bidder(bidder)
                    .amount(message.getAmount())
                    .bidTime(LocalDateTime.now())
                    .build();

            // Save bid
            bidRepository.save(bid);

            auction.addBid(bid);
            auctionRepository.save(auction);

            log.info("Bid saved: {}", bid.getId());


            // Get previous highest bidder
            var previousHighest =
            	    bidRepository.findTopByAuctionIdOrderByAmountDesc(auction.getId());

            if (previousHighest.isPresent()
                    && previousHighest.get().getId() != bid.getId()) {

                User previousBidder = previousHighest.get().getBidder();

                if (previousBidder.getId() != bidder.getId()) {

                    // Create notification
                    OutbidNotificationDto notification =
                            new OutbidNotificationDto(
                                    auction.getId(),
                                    auction.getItem().getTitle(),
                                    previousBidder.getUsername(),
                                    bid.getAmount(),
                                    bidder.getUsername(),
                                    bid.getBidTime()
                            );

                    // Send to RabbitMQ
                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.NOTIFICATION_EXCHANGE,
                            RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                            notification
                    );

                    log.info("Outbid notification sent to {}",
                            previousBidder.getUsername());


                    // Push to WebSocket (frontend live update)
                    messagingTemplate.convertAndSend(
                            "/topic/auction/" + auction.getId(),
                            notification
                    );
                }
            }

        } catch (Exception e) {

            log.error("Bid processing failed", e);

            // Do NOT requeue on business error
            // Acknowledge and drop message
        }
    }
}