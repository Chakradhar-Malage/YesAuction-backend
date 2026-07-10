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
        // FIX: was userRepository.findById(dto.getAuctionId()) — looked up a
        // User by an auction's ID. Must look up the outbid user by username.
        User user = userRepository.findByUsername(dto.getOutbidUsername())
                .orElseThrow(() -> new RuntimeException(
                        "User not found: " + dto.getOutbidUsername()));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Outbid Alert");
        notification.setMessage(
                String.format("%s outbid you with $%s on \"%s\"",
                        dto.getNewBidderUsername(),
                        dto.getNewAmount(),
                        dto.getAuctionTitle())
        );
        notification.setType(NotificationType.OUTBID);
        // FIX: was "/auctions/" + id (plural) — route is "/auction/:id" (singular).
        // Every notification link was a dead 404 before this fix.
        notification.setLink("/auction/" + dto.getAuctionId());

        Notification saved = notificationRepository.save(notification);

        // Real-time WebSocket push — now actually reachable, since
        // StompAuthInterceptor attaches a Principal on CONNECT.
        messagingTemplate.convertAndSendToUser(
                user.getUsername(),
                "/queue/notifications",
                saved
        );
    }
}