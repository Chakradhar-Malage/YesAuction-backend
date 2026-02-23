package com.Chakradhar.YesAuction.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auctions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private BigDecimal startingPrice;

    private BigDecimal currentPrice;  // starts as startingPrice

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status = AuctionStatus.ACTIVE;  // we'll define enum next

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids = new ArrayList<>();

    public void addBid(Bid bid) {
        bids.add(bid);
        bid.setAuction(this);
        this.currentPrice = bid.getAmount();
    }
}