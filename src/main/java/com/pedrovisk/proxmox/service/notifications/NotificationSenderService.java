package com.pedrovisk.proxmox.service.notifications;

import com.pedrovisk.proxmox.models.NotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NotificationSenderService {


    private final List<NotificationSender> notificators;

    @Autowired
    public NotificationSenderService(List<NotificationSender> notificators) {
        this.notificators = notificators;
    }

    public void sendNotification(NotificationDTO notificationDTO, String notificationType) {
        //TODO Implement another types of notifications, hightemperature...
        log.info("====== SENDING NOTIFICATION ====== ");
        log.info("NOTIFIERS SIZE = " + notificators.size());
        log.info("NOTIFIERS = " + notificators);
        log.info("====== SENDING NOTIFICATION ====== ");
        notificators.forEach(notifier -> notifier.sendHighMemoryNotification(notificationDTO));
    }

}

