package com.pedrovisk.proxmox.repository;


import lombok.Data;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Repository
public class NotificationSentReminderInMemoryRepository {


    //ComponentID, Sent Date
    private static final Map<String, Instant> notifationsReminder = new HashMap<>();

    public Instant get(String componentId) {
        return notifationsReminder.get(componentId);
    }

    public void insert(String componentId, Instant instant) {
        notifationsReminder.put(componentId, instant);
    }


}
