package com.Chakradhar.YesAuction.consumer;

import com.Chakradhar.YesAuction.config.RabbitMQConfig;
import com.Chakradhar.YesAuction.dto.OutbidNotificationDto;
import com.Chakradhar.YesAuction.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    public NotificationConsumer(SimpMessagingTemplate messagingTemplate, UserRepository userRepository) {
		super();
		this.messagingTemplate = messagingTemplate;
		this.userRepository = userRepository;
	}

	private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    // constructor injection

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void sendOutbidNotification(OutbidNotificationDto notification) {
        // In real app: find user email by username and send email (JavaMailSender)
        // For now: send private WebSocket message to outbid user

        String destination = "/user/" + notification.getOutbidUsername() + "/queue/notifications";

        messagingTemplate.convertAndSendToUser(
                notification.getOutbidUsername(),
                "/queue/notifications",
                notification
        );

        // Also log
        System.out.println("Outbid notification queued for " + notification.getOutbidUsername() +
                ": You were outbid on '" + notification.getAuctionTitle() + "' by " +
                notification.getNewBidderUsername() + " with $" + notification.getNewAmount());
    }
}