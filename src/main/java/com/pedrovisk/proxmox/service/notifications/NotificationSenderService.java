package com.pedrovisk.proxmox.service.notifications;

import com.pedrovisk.proxmox.models.notification.NotificationBase;
import com.pedrovisk.proxmox.models.notification.NotificationEntity;
import com.pedrovisk.proxmox.repository.NotificationSentReminderInMemoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class NotificationSenderService {


    private final List<NotificationSender> notificators;
    private final NotificationSentReminderInMemoryRepository repository;


    @Autowired
    public NotificationSenderService(List<NotificationSender> notificators, NotificationSentReminderInMemoryRepository repository) {
        this.notificators = notificators;
        this.repository = repository;
    }

    public void sendNotification(NotificationBase notificationBase) {

        log.debug("====== SENDING NOTIFICATION ====== ");
        log.debug("NOTIFIERS SIZE = {}", notificators.size());
        log.debug("NOTIFIERS = {}", notificators);

        var notificationId = notificationBase.getComponentId() + notificationBase.getValueType();
        var now = Instant.now();

        //TODO add a flag to ignore or send NotificationErrors if enabled

        //TODO add more information to guarantee that is the same notification.
        //TODO verify in another place after notification sent, because there is no guarantee it
        //   was sent and MAYBE we want for each notificator to have a different time reminder

        if (repository.contains(notificationId)) {
            var lastNotification = repository.get(notificationId);

            log.debug("Notification lastdate: {}", lastNotification.getNotificationDate());
            log.debug("Notification now: {}", now);

            //TODO add MaxNotificationQuantity flag with a value to check if a notification was sent x times even
            // before the 5 minutes. Change 5 minutes to a configuration too?
            if (now.isBefore(lastNotification.notificationDate.plus(5, ChronoUnit.MINUTES))) {

                repository.insert(notificationId, NotificationEntity.builder()
                        .notificationDate(lastNotification.notificationDate)
                        .quantity(lastNotification.quantity + 1).build());

                log.debug("Increasing notification quantity value");
                return;
            }

        }

        repository.insert(notificationId, NotificationEntity.builder().notificationDate(now)
                .quantity(1).build());
        notificators.forEach(notifier -> notifier.sendNotification(notificationBase));
        log.debug("NOTIFICATION SENT AND SAVED IN THE DATABASE");

        log.debug("====== SENDING NOTIFICATION ====== ");

    }

}

