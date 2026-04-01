package com.Chakradhar.YesAuction.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Chakradhar.YesAuction.dto.ChangePasswordRequest;
import com.Chakradhar.YesAuction.dto.MyAuctionsResponse;
import com.Chakradhar.YesAuction.dto.MyBidsResponse;
import com.Chakradhar.YesAuction.dto.MyProfileResponse;
import com.Chakradhar.YesAuction.dto.UpdateProfileRequest;
import com.Chakradhar.YesAuction.dto.UpdateProfileResponse;
import com.Chakradhar.YesAuction.dto.UserProfileResponse;
import com.Chakradhar.YesAuction.entity.User;
import com.Chakradhar.YesAuction.service.ProfileService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
	
	//auctions created by currentusre
	@GetMapping("/me/auctions")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Page<MyAuctionsResponse>> getMyAuctions(
			@AuthenticationPrincipal User currentUser,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "7") int size
			){
		
		Pageable pageable = PageRequest.of(page, size, Sort.by("endTime").descending());
		Page<MyAuctionsResponse> result = profileService.getMyAuctions(currentUser, pageable);
		return ResponseEntity.ok(result);
				
	}
	
	//total bids placed by currentuser on all auctions
	@GetMapping("/me/bids")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Page<MyBidsResponse>> getMyBids(
	        @AuthenticationPrincipal User currentUser,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "7") int size) {

	    Pageable pageable = PageRequest.of(page, size, Sort.by("bidTime").descending());
	    Page<MyBidsResponse> result = profileService.getMyBids(currentUser, pageable);
	    return ResponseEntity.ok(result);
	}
	
	
	@PutMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<UpdateProfileResponse> updateProfile(
	        @AuthenticationPrincipal User currentUser,
	        @Valid @RequestBody UpdateProfileRequest request) {

	    UpdateProfileResponse response = profileService.updateProfile(currentUser, request);
	    return ResponseEntity.ok(response);
	}
	
	@PutMapping("/me/password")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<String> changePassword(
	        @AuthenticationPrincipal User currentUser,
	        @Valid @RequestBody ChangePasswordRequest request) {

	    String message = profileService.changePassword(currentUser, request);
	    return ResponseEntity.ok(message);
	}
	
}
