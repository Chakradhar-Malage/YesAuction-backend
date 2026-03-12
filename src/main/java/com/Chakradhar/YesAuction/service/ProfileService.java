package com.Chakradhar.YesAuction.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.Chakradhar.YesAuction.dto.MyProfileResponse;
import com.Chakradhar.YesAuction.dto.UserProfileResponse;
import com.Chakradhar.YesAuction.entity.User;
import com.Chakradhar.YesAuction.repository.UserRepository;

@Service
public class ProfileService {
	private final UserRepository userRepository;
	
	public ProfileService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public MyProfileResponse getMyProfile(User currentUser) {
		return new MyProfileResponse (
					currentUser.getId(),
					currentUser.getUsername(),
					currentUser.getEmail(),
					currentUser.getRoles(),
					null, //add createdAt later 
					0, //total auctionsCreated
					0 //totalBidsPlaced
				);
	}
	
	public UserProfileResponse getPublicProfile(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));
				
		return new UserProfileResponse(
					user.getId(),
					user.getUsername(),
					user.getRoles().get(0),
					0, 
					0
				);				
	}
	
	public List<UserProfileResponse> getAllUSersForAdmin(){
		return userRepository.findAll().stream().map(user -> new UserProfileResponse (
					user.getId(), 
					user.getUsername(),
					user.getRoles().get(0),
					0, 
					0
				))
				.collect(Collectors.toList());
	}
}
