package com.Chakradhar.YesAuction.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import com.Chakradhar.YesAuction.entity.AuctionCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateAuctionRequest {

    @NotBlank
    private String title;

    private String description;

    private MultipartFile image;           // ← NEW

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal startingPrice;

    @Future
    private LocalDateTime endTime;

    private AuctionCategory category = AuctionCategory.OTHER; // if you added category
}