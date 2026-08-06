package com.Chakradhar.YesAuction.repository;
import com.Chakradhar.YesAuction.entity.Bid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
public interface BidRepository extends JpaRepository<Bid, Long> {
    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId ORDER BY b.amount DESC")
    List<Bid> findByAuctionIdOrderByAmountDesc(Long auctionId);
    @Query("""
    	    SELECT b FROM Bid b 
    	    JOIN FETCH b.auction a 
    	    WHERE b.bidder.id = :bidderId 
    	    ORDER BY b.bidTime DESC
    	""")
    	Page<Bid> findByBidderId(Long bidderId, Pageable pageable);
    
    Optional<Bid> findTopByAuctionIdOrderByAmountDesc(Long auctionId);  // current highest bid

    // ==================== STATS / MyAuctions ====================

    @Query("SELECT COUNT(b) > 0 FROM Bid b WHERE b.auction.id = :auctionId")
    boolean existsByAuctionId(@Param("auctionId") Long auctionId);

    // Total bid volume currently sitting on this seller's ACTIVE listings
    // (what the Dashboard's "Active Bids" stat represents).
    @Query("""
        SELECT COUNT(b) FROM Bid b
        WHERE b.auction.seller.id = :sellerId
        AND b.auction.status = com.Chakradhar.YesAuction.entity.AuctionStatus.ACTIVE
        """)
    long countActiveBidsForSeller(@Param("sellerId") Long sellerId);
}