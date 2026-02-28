package com.Chakradhar.YesAuction.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuctionUpdateDto {
    private Long auctionId;
    private BigDecimal currentPrice;
    private BidUpdateDto latestBid;
    // can add bidCount, timeLeft, etc. later
}