package com.Chakradhar.YesAuction.dto;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class WatchlistResponse {
	private Long watchlistId;
	private Long auctionId;
	private String title;
	private String imageUrl;
	private BigDecimal currentPrice;
	private LocalDateTime endTime;
	private String status;
	private LocalDateTime addedAt;
}
