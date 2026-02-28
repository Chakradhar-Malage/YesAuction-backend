package com.Chakradhar.YesAuction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OutbidNotificationDto {
    private Long auctionId;
    private String auctionTitle;
    private String outbidUsername;
    private BigDecimal newAmount;
    private String newBidderUsername;
    private LocalDateTime timestamp;
}