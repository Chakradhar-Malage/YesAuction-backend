package com.Chakradhar.YesAuction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableCaching
@EnableMethodSecurity(prePostEnabled = true)
public class YesAuctionBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(YesAuctionBackendApplication.class, args);
	}
}
