package com.Chakradhar.YesAuction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionSearchResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private BigDecimal currentPrice;
    private LocalDateTime endTime;
    private String sellerUsername;
    private String status;
}