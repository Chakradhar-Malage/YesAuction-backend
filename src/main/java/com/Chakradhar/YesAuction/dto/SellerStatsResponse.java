package com.Chakradhar.YesAuction.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerStatsResponse {
    private long totalAuctions;
    private long activeBids;      // total bids currently sitting on this seller's ACTIVE auctions
    private BigDecimal totalEarnings; // sum of currentPrice for ENDED auctions that received at least one bid
}