package com.Chakradhar.YesAuction.service;


import com.Chakradhar.YesAuction.entity.Auction;
import com.Chakradhar.YesAuction.entity.User;
import com.Chakradhar.YesAuction.entity.Watchlist;
import com.Chakradhar.YesAuction.dto.WatchlistResponse;
import com.Chakradhar.YesAuction.repository.AuctionRepository;
import com.Chakradhar.YesAuction.repository.WatchlistRepository;
import com.Chakradhar.YesAuction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class WatchlistService {
	private final WatchlistRepository watchlistRepository;
	private final AuctionRepository auctionRepository;
	private final UserRepository userRepository;
	
	@Transactional
	public void addtoWatchlist(Long userId, Long auctionId) {
		if(watchlistRepository.existsByUserIdAndAuctionId(userId, auctionId)) {
			return; //since its already there in the watchlist bro
		}
		
		Auction auction = auctionRepository.findById(auctionId)
				.orElseThrow(() -> new RuntimeException("Aucton not found"));
		
		Watchlist watchlist = new Watchlist();
		User user = userRepository.getReferenceById(userId);
		watchlist.setUser(user);  //referring id only
		watchlist.setAuction(auction);
		
		watchlistRepository.save(watchlist);
	}
	
	@Transactional
	public void removeFromWatchlist(Long userId, Long auctionId) {
		watchlistRepository.deleteByUserIdAndAuctionId(userId, auctionId);
	}
	
	public List<WatchlistResponse> getUserWatchlist(Long userId){
		return watchlistRepository.findByUserId(userId)
				.stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}
	
	private WatchlistResponse convertToResponse(Watchlist w) {
		Auction a = w.getAuction();
		return new WatchlistResponse(
				w.getId(),
				a.getId(),
				a.getItem().getTitle(),
				a.getItem().getImageUrl(),
				a.getCurrentPrice(),
				a.getEndTime(),
				a.getStatus().name(),
				w.getAddedAt()
		);
	}
	
	public boolean isInWatchlist(Long userId, Long auctionId) {
		return watchlistRepository.existsByUserIdAndAuctionId(userId, auctionId);
	}
}
