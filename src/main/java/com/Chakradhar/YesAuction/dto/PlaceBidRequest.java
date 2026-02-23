package com.Chakradhar.YesAuction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlaceBidRequest {
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
}