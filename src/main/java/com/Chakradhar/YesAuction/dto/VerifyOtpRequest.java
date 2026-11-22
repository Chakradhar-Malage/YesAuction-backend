package com.Chakradhar.YesAuction.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {
	@NotBlank
	private String mobileNumber;
	
	@NotBlank
	private String otp;
}
