package com.Chakradhar.YesAuction.service;

import com.Chakradhar.YesAuction.dto.OutbidNotificationDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOutbidEmail(OutbidNotificationDto notification, String recipientEmail) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        try {
            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("You were outbid on " + notification.getAuctionTitle() + "!");

            String htmlBody = """
                <h2>Outbid Notification</h2>
                <p>Hello,</p>
                <p>You have been outbid on auction: <strong>%s</strong></p>
                <p>New bid: <strong>$%.2f</strong> by %s</p>
                <p>Time: %s</p>
                <p>Quickly place a higher bid: <a href="http://localhost:3000/auction/%d">View Auction</a></p>
                <p>Best regards,<br>YesAuction Team</p>
                """.formatted(
                    notification.getAuctionTitle(),
                    notification.getNewAmount(),
                    notification.getNewBidderUsername(),
                    notification.getTimestamp(),
                    notification.getAuctionId()
                );

            helper.setText(htmlBody, true); // true = HTML

            mailSender.send(message);
            System.out.println("Outbid email sent to " + recipientEmail);
        } catch (MessagingException e) {
            System.err.println("Failed to send outbid email: " + e.getMessage());
        }
    }

    // Later: add winner email, etc.
}