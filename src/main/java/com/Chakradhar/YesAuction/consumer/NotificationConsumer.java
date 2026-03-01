package com.Chakradhar.YesAuction.consumer;

import com.Chakradhar.YesAuction.config.RabbitMQConfig;
import com.Chakradhar.YesAuction.dto.OutbidNotificationDto;
import com.Chakradhar.YesAuction.entity.User;
import com.Chakradhar.YesAuction.repository.UserRepository;
import com.Chakradhar.YesAuction.service.EmailService;

import jakarta.mail.MessagingException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {
	private final EmailService emailService;
    public NotificationConsumer(
    		SimpMessagingTemplate messagingTemplate, 
    		UserRepository userRepository,
    		EmailService emailService) {
		super();
		this.emailService = emailService;
		this.messagingTemplate = messagingTemplate;
		this.userRepository = userRepository;
	}

	private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    // constructor injection

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void sendOutbidNotification(OutbidNotificationDto notification) throws MessagingException {
        // Find the outbid user's email
        User outbidUser = userRepository.findByUsername(notification.getOutbidUsername())
                .orElse(null);

        if (outbidUser != null && outbidUser.getEmail() != null) {
            emailService.sendOutbidEmail(notification, outbidUser.getEmail());

            // Also send private WebSocket notification (optional)
            messagingTemplate.convertAndSendToUser(
                    notification.getOutbidUsername(),
                    "/queue/notifications",
                    notification
            );
        } else {
            System.out.println("No email found for user: " + notification.getOutbidUsername());
        }
    }
}