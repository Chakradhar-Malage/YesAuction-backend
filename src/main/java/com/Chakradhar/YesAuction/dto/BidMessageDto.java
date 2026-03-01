package com.Chakradhar.YesAuction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class BidMessageDto {

    private String messageId;
    public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Long getAuctionId() {
		return auctionId;
	}

	public void setAuctionId(Long auctionId) {
		this.auctionId = auctionId;
	}

	public Long getBidderId() {
		return bidderId;
	}

	public void setBidderId(Long bidderId) {
		this.bidderId = bidderId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	private Long auctionId;
    private Long bidderId;
    private BigDecimal amount;
    
    public BidMessageDto(
            String messageId,
            Long auctionId,
            Long bidderId,
            BigDecimal amount
        ) {
            this.messageId = messageId;
            this.auctionId = auctionId;
            this.bidderId = bidderId;
            this.amount = amount;
        }
}