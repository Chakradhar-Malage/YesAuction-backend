package com.Chakradhar.YesAuction.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {
	private final RedisTemplate<String, String> redisTemplate;
	private static final String BLACKLIST_PREFIX = "blacklist:token:";
	
	public TokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	//blacklist token for its remaining timeline
	public void blacklistToken(String token, long expirationInMillis) {
		String key = BLACKLIST_PREFIX + token;
		redisTemplate.opsForValue().set(key, "blacklisted", expirationInMillis, TimeUnit.MILLISECONDS);
	}
	
	//check if token is blacklisted
	public boolean isBlacklisted(String token) {
		String key = BLACKLIST_PREFIX + token;
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}
}
