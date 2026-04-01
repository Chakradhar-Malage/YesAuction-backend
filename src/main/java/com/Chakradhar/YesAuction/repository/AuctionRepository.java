package com.Chakradhar.YesAuction.repository;

import com.Chakradhar.YesAuction.entity.Auction;
import com.Chakradhar.YesAuction.entity.AuctionStatus;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByStatus(AuctionStatus status);
    Page<Auction> findByStatus(AuctionStatus status, Pageable pageable);

    @Query("SELECT a FROM Auction a WHERE a.seller.id = :sellerId")
    List<Auction> findBySellerId(Long sellerId);
    
    @Query("SELECT a FROM Auction a WHERE a.seller.id = :sellerId ORDER BY a.endTime DESC")
    Page<Auction> findBySellerId(Long sellerId, Pageable pageable);
    
 // Search auctions by title (case-insensitive) + only ACTIVE auctions
    @Query("SELECT a FROM Auction a " +
           "WHERE a.status = 'ACTIVE' " +
           "AND LOWER(a.item.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY a.endTime DESC")
    Page<Auction> searchActiveAuctionsByTitle(@Param("keyword") String keyword, Pageable pageable);
    
}