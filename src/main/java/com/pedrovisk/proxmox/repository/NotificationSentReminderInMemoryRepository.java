package com.pedrovisk.proxmox.repository;


import com.pedrovisk.proxmox.models.notification.NotificationEntity;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Data
@Repository
public class NotificationSentReminderInMemoryRepository {


    //ComponentID, Sent Date
    private static final Map<String, NotificationEntity> notificationsReminder = new HashMap<>();

    public NotificationEntity get(String componentId) {
        return notificationsReminder.get(componentId);
    }

    public boolean contains(String componentId){
         return notificationsReminder.containsKey(componentId);
    }

    public void insert(String componentId, NotificationEntity notificationEntity) {
        notificationsReminder.put(componentId, notificationEntity);
    }


}
