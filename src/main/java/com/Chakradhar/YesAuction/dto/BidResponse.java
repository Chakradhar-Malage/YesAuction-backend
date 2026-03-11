package com.Chakradhar.YesAuction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BidResponse {
//	@Id
	private Long id;
	private BigDecimal amount;
	private String bidderUsername;
	private LocalDateTime bidTime;
}
