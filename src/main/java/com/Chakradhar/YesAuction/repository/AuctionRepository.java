package com.Chakradhar.YesAuction.repository;

import com.Chakradhar.YesAuction.entity.Auction;
import com.Chakradhar.YesAuction.entity.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByStatus(AuctionStatus status);

    @Query("SELECT a FROM Auction a WHERE a.seller.id = :sellerId")
    List<Auction> findBySellerId(Long sellerId);
}