package com.Chakradhar.YesAuction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyAuctionsResponse {
	private Long id;
	private String title;
	private String description;
	private BigDecimal currentPrice;
	private String imageUrl;
	private LocalDateTime endTime;
	private String status;
}
