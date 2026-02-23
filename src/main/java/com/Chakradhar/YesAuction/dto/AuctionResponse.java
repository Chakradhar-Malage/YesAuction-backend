package com.Chakradhar.YesAuction.dto;

import com.Chakradhar.YesAuction.entity.AuctionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AuctionResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private BigDecimal currentPrice;
    private LocalDateTime endTime;
    private AuctionStatus status;
    private String sellerUsername;
    // can add highestBidder, bidCount later
}