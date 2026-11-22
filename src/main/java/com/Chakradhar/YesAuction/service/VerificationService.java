package com.Chakradhar.YesAuction.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.Chakradhar.YesAuction.entity.User;
import com.Chakradhar.YesAuction.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationService {
	private final UserRepository userRepository;
	
	// private final SmsService smsService;
	
	public void sendMobileOtp(User user, String mobileNumber) {
		String otp = String.valueOf(100000 + new Random().nextInt(900000)); //6 digit
		
		user.setMobileNumber(mobileNumber);
		user.setOtp(otp);
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
		userRepository.save(user);
		
		//Todo : send real SMS to user later via sendSMS service
		System.out.println("OTP for " + mobileNumber + " is: " + otp);
	}
	
	public boolean verifyMobileOtp(User user, String mobileNumber, String otp) {
		if(user.getOtp() == null || user.getOtpExpiry() == null) {
			throw new RuntimeException("No OTP requested");
		}
		
		if(LocalDateTime.now().isAfter(user.getOtpExpiry())) {
			throw new RuntimeException("OTP is expired");
		}
		
		if(!user.getOtp().equals(otp) || !user.getMobileNumber().equals(mobileNumber)) {
			throw new RuntimeException("Invalid OTP");
		}
		
		user.setMobileNumberVerified(true);
		user.setOtp(null);
		user.setOtpExpiry(null);
		userRepository.save(user);
		
		return true;
	}
}
