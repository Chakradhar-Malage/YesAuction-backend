package com.Chakradhar.YesAuction.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Chakradhar.YesAuction.dto.MyProfileResponse;
import com.Chakradhar.YesAuction.dto.UserProfileResponse;
import com.Chakradhar.YesAuction.entity.User;
import com.Chakradhar.YesAuction.service.ProfileService;

@RestController
@RequestMapping("/api/users")
public class ProfileController {
	private final ProfileService profileService;
	
	public ProfileController(ProfileService profileService) {
		this.profileService = profileService;
	}
	
	// =========My Profile========
	@GetMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<MyProfileResponse> getMyProfile(@AuthenticationPrincipal User currentUser){
		MyProfileResponse profile = profileService.getMyProfile(currentUser);
		return ResponseEntity.ok(profile);
	}
	
	// ========Public profile======
	@GetMapping("/{username}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<UserProfileResponse> getPublicProfile(@PathVariable String username){
		UserProfileResponse profile = profileService.getPublicProfile(username);
		return ResponseEntity.ok(profile);
	}
	
	
	// ========For Admin========
	@GetMapping("/admin/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
		return ResponseEntity.ok(profileService.getAllUSersForAdmin());
	}
	
	
}
