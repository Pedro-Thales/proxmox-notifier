package com.pedrovisk.proxmox.service.notifications;

import com.pedrovisk.proxmox.models.NotificationDTO;
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

    public void sendNotification(NotificationDTO notificationDTO, String notificationType) {
        //TODO Implement another types of notifications, hightemperature, turnedoff?...
        log.debug("====== SENDING NOTIFICATION ====== ");
        log.debug("NOTIFIERS SIZE = " + notificators.size());
        log.debug("NOTIFIERS = " + notificators);

        var lastDate = repository.get(notificationDTO.getComponentId());
        var now = Instant.now();

        log.debug("Notification lastdate: " + lastDate);
        log.debug("Notification now: " + now);

        if (lastDate == null || now.isAfter(lastDate.plus(5, ChronoUnit.MINUTES))) {
            notificators.forEach(notifier -> notifier.sendHighUsageNotification(notificationDTO));
            repository.insert(notificationDTO.getComponentId(), now);
            log.debug("NOTIFICATION SENT AND SAVED IN THE DATABASE");
        }

        log.debug("====== SENDING NOTIFICATION ====== ");

    }

}

