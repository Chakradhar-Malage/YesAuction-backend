package com.Chakradhar.YesAuction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
	private Long id;
	private String username;
	private String role;  //ROLE_USER or ROLE_ADMIN
	private int totalAuctionsCreated;
	private int totalBidsPlaced;
	
	public UserProfileResponse(String username) {
		super();
		this.username = username;
	}
	
	
}
	