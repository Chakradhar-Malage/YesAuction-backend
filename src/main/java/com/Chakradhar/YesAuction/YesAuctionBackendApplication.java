package com.Chakradhar.YesAuction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class YesAuctionBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(YesAuctionBackendApplication.class, args);
	}

}
