package com.Chakradhar.YesAuction.service;

import com.Chakradhar.YesAuction.dto.RegisterRequest;
import com.Chakradhar.YesAuction.entity.User;
import com.Chakradhar.YesAuction.repository.*;

import java.util.List;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.*;

@Service
public class UserService implements UserDetailsService{
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	public User register(RegisterRequest request) {
		if(userRepository.existsByUsername(request.getUsername())) {
			throw new RuntimeException("Username already exists!");
		}
		
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already exists!");
		}
		
		User user = User.builder()
				.username(request.getUsername())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.roles(List.of("ROLE_USER"))
				.build();
		
		return userRepository.save(user);
	}
	
	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
		return userRepository.findByUsername(usernameOrEmail)
				.or(() -> userRepository.findByEmail(usernameOrEmail))
				.orElseThrow(() -> new UsernameNotFoundException("User not found "+ usernameOrEmail));
	}
	
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + username));
    }
}
