package com.Chakradhar.YesAuction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyProfileResponse {
	private Long id;
	private String username;
	private String email;
	private List<String> roles;
	private LocalDateTime createdAt;
	private int totalAuctionsCreated;
	private int totalBidsPlaced;
}
