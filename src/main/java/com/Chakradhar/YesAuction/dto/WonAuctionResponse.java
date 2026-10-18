package com.Chakradhar.YesAuction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WonAuctionResponse {
    private Long auctionId;
    private String title;
    private String imageUrl;
    private BigDecimal winningAmount;
    private LocalDateTime endTime;
    private String sellerUsername;
}