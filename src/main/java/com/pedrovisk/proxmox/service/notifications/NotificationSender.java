package com.pedrovisk.proxmox.service.notifications;

import com.pedrovisk.proxmox.models.NotificationDTO;

public interface NotificationSender {

    void sendHighMemoryNotification(NotificationDTO notificationDTO);

}
