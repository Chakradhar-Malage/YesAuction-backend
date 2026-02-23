package com.Chakradhar.YesAuction.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;  // optional, can be S3 link later or local for now

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private Auction auction;
}