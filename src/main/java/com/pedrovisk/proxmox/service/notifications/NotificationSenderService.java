package com.pedrovisk.proxmox.service.notifications;

import com.pedrovisk.proxmox.models.notification.NotificationBase;
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

        //TODO add a flag to ignore or send NotificationErrors if enabled

        //Verify if we already sent this notification in the last 5 minutes.
        //TODO add more information to guarantee that is the same notification.
        //TODO verify in another place after notification sent, because there is no guarantee it
        //   was sent and MAYBE we want for each notificator to have a different time reminder

        var lastDate = repository.get(notificationBase.getComponentId());
        var now = Instant.now();

        log.debug("Notification lastdate: {}", lastDate);
        log.debug("Notification now: {}", now);

        if (lastDate == null || now.isAfter(lastDate.plus(5, ChronoUnit.MINUTES))) {
            notificators.forEach(notifier -> notifier.sendNotification(notificationBase));

            repository.insert(notificationBase.getComponentId() + notificationBase.getValueType(), now);
            log.debug("NOTIFICATION SENT AND SAVED IN THE DATABASE");
        }

        log.debug("====== SENDING NOTIFICATION ====== ");

    }

}

