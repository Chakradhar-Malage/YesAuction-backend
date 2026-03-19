package com.Chakradhar.YesAuction.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.Chakradhar.YesAuction.dto.MyAuctionsResponse;
import com.Chakradhar.YesAuction.dto.MyBidsResponse;
import com.Chakradhar.YesAuction.dto.MyProfileResponse;
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
	
	public ProfileService(UserRepository userRepository, 
							GlobalExceptionHandler globalExceptionHandler,
							AuctionRepository auctionRepository,
							BidRepository bidsRepository) {
		this.userRepository = userRepository;
		this.globalExceptionHandler = globalExceptionHandler;
		this.auctionRepository = auctionRepository;
		this.bidRepository = bidsRepository;
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
		
		return auctions.map(a -> new MyAuctionsResponse(
					a.getId(),
					a.getItem().getTitle(),
					a.getItem().getDescription(),
		            a.getCurrentPrice(),
		            a.getEndTime(),
		            a.getStatus().name()
				));
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
}
