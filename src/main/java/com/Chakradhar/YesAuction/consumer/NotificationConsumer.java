package com.Chakradhar.YesAuction.consumer;

import com.Chakradhar.YesAuction.dto.OutbidNotificationDto;
import com.Chakradhar.YesAuction.entity.Notification;
import com.Chakradhar.YesAuction.entity.NotificationType;
import com.Chakradhar.YesAuction.entity.User;
import com.Chakradhar.YesAuction.repository.NotificationRepository;
import com.Chakradhar.YesAuction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "notificationQueue")
    public void handleNotification(OutbidNotificationDto dto) {
        User user = userRepository.findById(dto.getAuctionId()) // Temporary - change later to actual userId
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Outbid Alert");
        notification.setMessage(
            String.format("%s has been outbid with $%s on auction ID: %d", 
                dto.getNewBidderUsername(), dto.getNewAmount(), dto.getAuctionId())
        );
        notification.setType(NotificationType.OUTBID);
        notification.setLink("/auctions/" + dto.getAuctionId());

        Notification saved = notificationRepository.save(notification);

        // Real-time WebSocket push
        messagingTemplate.convertAndSendToUser(
            user.getUsername(),
            "/queue/notifications",
            saved
        );
    }
}