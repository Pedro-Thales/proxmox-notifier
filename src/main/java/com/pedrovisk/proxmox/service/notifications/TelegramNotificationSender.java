package com.pedrovisk.proxmox.service.notifications;

import com.pedrovisk.proxmox.configuration.TelegramProperties;
import com.pedrovisk.proxmox.models.NotificationDTO;
import com.pedrovisk.proxmox.telegram.TelegramApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Slf4j
@RequiredArgsConstructor
@Qualifier("telegramNotification")
@ConditionalOnProperty(name = "notification.telegram.enabled", havingValue = "true")
public class TelegramNotificationSender implements NotificationSender{


    private final TelegramApi telegramApi;
    private final TelegramProperties telegramProperties;

    public void sendMessageToTelegram(NotificationDTO notificationDTO) {
        try {
            //TODO send maybe in html to be more easy to convert to email too
            // https://stackoverflow.com/questions/38119481/send-bold-italic-text-on-telegram-bot-with-html

            var message = STR.
                    """
                        \{notificationDTO.componentType()}: \{notificationDTO.componentId()} with used \{notificationDTO.valueType()} getting dangerous

                            Actual used: \{String.valueOf(notificationDTO.actualValue())}
                            Threshold: \{notificationDTO.threshold()}
                        """;

            String escapedMessage = message
                    .replace(".", "\\.")
                    .replace("-", "\\-")
                    .replace("Actual used:", "*Actual used:*")
                    .replace("Threshold:", "*Threshold:*");

            var response = telegramApi.sendMessageToBotChatDefault(escapedMessage);
            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Error while sending message to telegram! Response: {} ", response);
                throw new TelegramApiException("Status was not OK");
            }
        } catch (Exception e) {
            log.error("Not able to sent message to telegram, check logs to see more information! ", e);
        }

    }

    @Override
    public void sendHighMemoryNotification(NotificationDTO notificationDTO) {
        sendMessageToTelegram(notificationDTO);
    }
}
