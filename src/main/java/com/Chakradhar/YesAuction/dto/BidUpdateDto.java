package com.Chakradhar.YesAuction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BidUpdateDto {
    private BigDecimal amount;
    private String bidderUsername;
    private LocalDateTime bidTime;
}