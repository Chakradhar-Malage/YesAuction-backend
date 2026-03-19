package com.Chakradhar.YesAuction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyBidsResponse {
	private Long bidId;
	private Long auctionId;
	private String auctionTitle;
	private BigDecimal amount;
	private LocalDateTime bidTime;
	private BigDecimal currentAuctionPrice;
}
