package com.Chakradhar.YesAuction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyAuctionsResponse {
	private Long id;
	private String title;
	private String description;
	private BigDecimal currentPrice;
	private LocalDateTime endTime;
	private String status;
}
