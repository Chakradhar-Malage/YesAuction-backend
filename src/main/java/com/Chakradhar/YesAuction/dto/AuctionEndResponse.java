package com.Chakradhar.YesAuction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionEndResponse {
    private Long auctionId;
    private String title;
    private BigDecimal finalPrice;
    private String winnerUsername;
    private LocalDateTime endedAt;
    private String message;
}