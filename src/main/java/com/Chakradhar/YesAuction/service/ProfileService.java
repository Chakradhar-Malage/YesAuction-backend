package com.Chakradhar.YesAuction.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Chakradhar.YesAuction.dto.ChangePasswordRequest;
import com.Chakradhar.YesAuction.dto.MyAuctionsResponse;
import com.Chakradhar.YesAuction.dto.MyBidsResponse;
import com.Chakradhar.YesAuction.dto.MyProfileResponse;
import com.Chakradhar.YesAuction.dto.UpdateProfileRequest;
import com.Chakradhar.YesAuction.dto.UpdateProfileResponse;
import com.Chakradhar.YesAuction.dto.UserProfileResponse;
import com.Chakradhar.YesAuction.entity.Auction;
import com.Chakradhar.YesAuction.entity.Bid;
import com.Chakradhar.YesAuction.entity.User;
import com.Chakradhar.YesAuction.exception.*;
import com.Chakradhar.YesAuction.repository.AuctionRepository;
import com.Chakradhar.YesAuction.repository.BidRepository;
import com.Chakradhar.YesAuction.repository.UserRepository;

@Service
public class ProfileService {

	private final GlobalExceptionHandler globalExceptionHandler;
	private final UserRepository userRepository;
	private final AuctionRepository auctionRepository;
	private final BidRepository bidRepository;
	private final PasswordEncoder passwordEncoder;
	
	public ProfileService(UserRepository userRepository, 
							GlobalExceptionHandler globalExceptionHandler,
							AuctionRepository auctionRepository,
							BidRepository bidsRepository,
							PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.globalExceptionHandler = globalExceptionHandler;
		this.auctionRepository = auctionRepository;
		this.bidRepository = bidsRepository;
		this.passwordEncoder = passwordEncoder;
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
	
	public Page<MyAuctionsResponse> getMyAuctions(User currentUser, Pageable pageable){
		Page<Auction> auctions = auctionRepository.findBySellerId(currentUser.getId(), pageable);
		
		return auctions.map(a -> {
		    String status;

		    if (a.getEndTime().isBefore(LocalDateTime.now())) {
		        status = "ENDED";
		    } else {
		        status = "ACTIVE";
		    }

		    return new MyAuctionsResponse(
		        a.getId(),
		        a.getItem().getTitle(),
		        a.getItem().getDescription(),
		        a.getCurrentPrice(),
		        a.getEndTime(),
		        status
		    );
		});
	}
	
	public Page<MyBidsResponse> getMyBids(User currentUser, Pageable pageable) {
	    Page<Bid> bids = bidRepository.findByBidderId(currentUser.getId(), pageable);

	    return bids.map(b -> new MyBidsResponse(
	            b.getId(),
	            b.getAuction().getId(),
	            b.getAuction().getItem().getTitle(),
	            b.getAmount(),
	            b.getBidTime(),
	            b.getAuction().getCurrentPrice()
	    ));
	}
	
	@Transactional
	public UpdateProfileResponse updateProfile(User currentUser, UpdateProfileRequest request) {

	    // Check if email is already taken by another user
	    if (!currentUser.getEmail().equalsIgnoreCase(request.getEmail())) {
	        if (userRepository.existsByEmail(request.getEmail())) {
	            throw new RuntimeException("Email is already in use by another user");
	        }
	    }

	    // Check if username is already taken by another user
	    if (!currentUser.getUsername().equals(request.getUsername())) {
	        if (userRepository.existsByUsername(request.getUsername())) {
	            throw new RuntimeException("Username is already taken");
	        }
	    }

	    currentUser.setUsername(request.getUsername());
	    currentUser.setEmail(request.getEmail());

	    userRepository.save(currentUser);

	    return new UpdateProfileResponse(
	            currentUser.getUsername(),
	            currentUser.getEmail(),
	            "Profile updated successfully"
	    );
	}
	
	
	@Transactional
	public String changePassword(User currentUser, ChangePasswordRequest request) {

	    // Verify current password
	    if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
	        throw new RuntimeException("Current password is incorrect");
	    }

	    // Update with new password (hashed)
	    currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
	    userRepository.save(currentUser);

	    return "Password changed successfully";
	}
}
