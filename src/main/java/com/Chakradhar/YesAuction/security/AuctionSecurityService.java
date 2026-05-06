package com.Chakradhar.YesAuction.security;

import com.Chakradhar.YesAuction.repository.AuctionRepository;
import org.springframework.stereotype.Service;

@Service("auctionSecurityService")   // Important: name it like this
public class AuctionSecurityService {

    private final AuctionRepository auctionRepository;

    public AuctionSecurityService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    /**
     * Checks if the current user is the owner of the auction
     */
    public boolean isOwner(Long auctionId, String username) {
        return auctionRepository.findById(auctionId)
                .map(auction -> auction.getSeller().getUsername().equals(username))
                .orElse(false);
    }
}