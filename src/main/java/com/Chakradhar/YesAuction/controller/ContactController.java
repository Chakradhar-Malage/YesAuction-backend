package com.Chakradhar.YesAuction.controller;

import com.Chakradhar.YesAuction.dto.ContactRequest;
import com.Chakradhar.YesAuction.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<String> submitContact(@Valid @RequestBody ContactRequest request) {
        contactService.saveMessage(request);
        return ResponseEntity.ok("Message sent successfully!");
    }
}