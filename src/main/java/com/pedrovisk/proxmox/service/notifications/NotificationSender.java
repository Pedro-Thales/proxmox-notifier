package com.pedrovisk.proxmox.service.notifications;

import com.pedrovisk.proxmox.models.notification.NotificationBase;

public interface NotificationSender {

    void sendNotification(NotificationBase notificationBase);

}
