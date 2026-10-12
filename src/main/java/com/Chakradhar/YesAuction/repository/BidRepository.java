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
    
    
    List<Bid> findByBidderId(Long bidderId);

    @Query(value = """
        SELECT COUNT(*) FROM bids b
        INNER JOIN auctions a ON a.id = b.auction_id
        WHERE b.bidder_id = :userId
        AND a.status = 'ENDED'
        AND b.amount = (
            SELECT MAX(b2.amount) FROM bids b2 WHERE b2.auction_id = b.auction_id
        )
        """, nativeQuery = true)
    long countWonAuctions(@Param("userId") Long userId);
    
}

