package com.pedrovisk.proxmox.service.notifications;

import com.pedrovisk.proxmox.models.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
@Qualifier("emailNotification")
@ConditionalOnProperty(name = "notification.email.enabled", havingValue = "true")
public class EmailNotificationSender implements NotificationSender{

    private final JavaMailSender emailSender;

    public void sendEmailAsync(BigDecimal percentUsed) {
        Runnable runnable = () -> sendEmail(percentUsed);

        Thread.ofVirtual()
                .name("mailer-thread")
                .start(runnable);

    }

    public void sendEmailFake() throws InterruptedException {
        long initTime = System.currentTimeMillis();
        log.info("Sending Email! ");
        Thread.sleep(10000);
        long executionTime = System.currentTimeMillis() - initTime;
        log.info("Email sent in: " + executionTime + "ms" + " in the thread: " + Thread.currentThread().threadId());
    }


    private void sendEmail(BigDecimal percentUsed) {
        long initTime = System.currentTimeMillis();
        log.info("Email sending started! ");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("memory-usage@myserver.com");
        message.setTo("pedrottb01@gmail.com");
        message.setSubject("High memory usage");
        message.setText("Current memory usage is: " + percentUsed);
        log.info("Sending email! ");
        emailSender.send(message);
        log.info("Email sent! ");
        long executionTime = System.currentTimeMillis() - initTime;
        log.info("Email sent in: " + executionTime + "ms");
    }

    @Override
    public void sendHighMemoryNotification(NotificationDTO notificationDTO) {
        sendEmailAsync(notificationDTO.actualValue());
    }
}
