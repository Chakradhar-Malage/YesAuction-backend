package com.Chakradhar.YesAuction.repository;

import com.Chakradhar.YesAuction.entity.Watchlist;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface WatchlistRepository extends  JpaRepository<Watchlist, Long> {
	List<Watchlist> findByUserId(Long userId);
	Optional<Watchlist> findByUserIdAndAuctionId(Long userId, Long auctionId);
	boolean existsByUserIdAndAuctionId(Long userId, Long auctionId);
	void deleteByUserIdAndAuctionId(Long userId, Long auctionId);
}
