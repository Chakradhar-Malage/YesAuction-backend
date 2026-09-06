package com.Chakradhar.YesAuction.service;

import com.Chakradhar.YesAuction.dto.ContactRequest;
import com.Chakradhar.YesAuction.entity.ContactUsMessage;
import com.Chakradhar.YesAuction.repository.ContactUsMessageRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactUsMessageRepository contactMessageRepository;
    // private final EmailService emailService; // optional

    public void saveMessage(ContactRequest request) {
        ContactUsMessage message = ContactUsMessage.builder()
                .name(request.getName())
                .email(request.getEmail())
                .subject(request.getSubject())
                .message(request.getMessage())
                .build();

        contactMessageRepository.save(message);

    }
    
    public List<ContactUsMessage> getAllMessages() {
    	return contactMessageRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public void markAsRead(Long id) {
    	ContactUsMessage message = contactMessageRepository.findById(id)
    			.orElseThrow(() -> new RuntimeException("Message not found"));
    	
    	message.setRead(true);
    	contactMessageRepository.save(message);
    }
}