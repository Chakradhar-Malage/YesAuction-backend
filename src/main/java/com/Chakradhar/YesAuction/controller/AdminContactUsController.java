package com.Chakradhar.YesAuction.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Chakradhar.YesAuction.entity.ContactUsMessage;
import com.Chakradhar.YesAuction.service.ContactService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/contact-messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminContactUsController {
	private final ContactService contactService;
	
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<ContactUsMessage>> getAllMessages(){
		return ResponseEntity.ok(contactService.getAllMessages());
	}
	
	@PutMapping("{id}/read")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> markAsRead(@PathVariable Long id){
		contactService.markAsRead(id);
		return ResponseEntity.ok("Marked as read");
	}
}
