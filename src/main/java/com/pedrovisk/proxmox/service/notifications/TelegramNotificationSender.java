package com.pedrovisk.proxmox.service.notifications;

import com.pedrovisk.proxmox.configuration.TelegramProperties;
import com.pedrovisk.proxmox.models.notification.NotificationBase;
import com.pedrovisk.proxmox.telegram.TelegramApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Qualifier("telegramNotification")
@ConditionalOnProperty(name = "notification.telegram.enabled", havingValue = "true")
public class TelegramNotificationSender implements NotificationSender{


    private final TelegramApi telegramApi;
    private final TelegramProperties telegramProperties;

    public void sendMessageToTelegram(NotificationBase notificationBase) {
        try {
            //TODO send maybe in html to be more easy to convert to email too
            // https://stackoverflow.com/questions/38119481/send-bold-italic-text-on-telegram-bot-with-html

            String escapedMessage = notificationBase.getMessage()
                    .replace(".", "\\.")
                    .replace("-", "\\-")
                    .replace("Actual used:", "*Actual used:*")
                    .replace("Threshold:", "*Threshold:*");

            var response = telegramApi.sendMessageToBotChatDefault(escapedMessage);
            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Error while sending message to telegram! Response: {} ", response);
                throw new Exception("Status was not OK");
            }
        } catch (Exception e) {
            log.error("Not able to sent message to telegram, check logs to see more information! ", e);
        }

    }

    @Override
    public void sendNotification(NotificationBase notificationBase) {
        sendMessageToTelegramAsync(notificationBase);
    }

    public void sendMessageToTelegramAsync(NotificationBase notificationBase) {
        Runnable runnable = () -> sendMessageToTelegram(notificationBase);

        Thread.ofVirtual()
                .name("mailer-thread")
                .start(runnable);

    }

}
