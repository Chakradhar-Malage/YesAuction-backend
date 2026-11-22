package com.Chakradhar.YesAuction.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendOtpRequest {
	@NotBlank
	private String mobileNumber;
}
