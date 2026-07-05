//package com.Chakradhar.YesAuction.seed;
//
//import com.Chakradhar.YesAuction.entity.*;
//import com.Chakradhar.YesAuction.repository.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//@Profile("dev")   // Only runs in dev profile
//public class DataSeeder implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final ItemRepository itemRepository;
//    private final AuctionRepository auctionRepository;
//    private final BidRepository bidRepository;
//    // Add other repositories as needed
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//        if (userRepository.count() > 0) {
//            log.info("Seed data already exists. Skipping...");
//            return;
//        }
//
//        log.info("Starting seed data...");
//
//        seedUsers();
//        seedItems();
//        seedAuctions();
//        seedBids();
//
//        log.info("✅ Seed data completed successfully!");
//    }
//
//    private void seedUsers() {
//        List<User> users = List.of(
//            User.builder()
//                .username("admin")
//                .email("admin@yesauction.com")
//                .password("$2a$10$...") // Use BCrypt encoded password
////                .roles("ADMIN")
////                .enabled(true)
//                .build(),
//
//            User.builder()
//                .username("buyer1")
//                .email("buyer1@yesauction.com")
//                .password("$2a$10$...") 
////                .roles("USER")
////                .enabled(true)
//                .build(),
//
//            User.builder()
//                .username("seller1")
//                .email("seller1@yesauction.com")
//                .password("$2a$10$...") 
////                .roles("SELLER")
////                .enabled(true)
//                .build()
//        );
//
//        userRepository.saveAll(users);
//        log.info("Seeded {} users", users.size());
//    }
//
//    private void seedItems() {
//        // Add your Item seeding logic here
//        Item item1 = Item.builder()
//                .title("Vintage Rolex Watch")
//                .description("Excellent condition, 1985 model")
////                .startingPrice(BigDecimal.valueOf(5000))
//                .category("Watches")
//                .seller(userRepository.findByUsername("seller1").orElseThrow())
//                .build();
//
//        itemRepository.saveAll(List.of(item1 /*, item2, ... */));
//    }
//
//    private void seedAuctions() {
//        // Example
//        Auction auction = Auction.builder()
//                .item()
//                .startTime(LocalDateTime.now().plusMinutes(5))
//                .endTime(LocalDateTime.now().plusDays(7))
//                .status(AuctionStatus.ACTIVE)
//                .currentPrice(BigDecimal.valueOf(5200))
//                .build();
//
//        auctionRepository.save(auction);
//    }
//
//    private void seedBids() {
//        // Optional
//    }
//}