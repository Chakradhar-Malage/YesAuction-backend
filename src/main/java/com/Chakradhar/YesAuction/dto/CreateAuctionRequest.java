package com.Chakradhar.YesAuction.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateAuctionRequest {

    @NotBlank
    private String title;

    private String description;

    private String imageUrl;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal startingPrice;

    @Future
    private LocalDateTime endTime;  // startTime = now()
}