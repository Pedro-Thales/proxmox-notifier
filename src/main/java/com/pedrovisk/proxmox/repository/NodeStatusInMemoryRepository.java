package com.pedrovisk.proxmox.repository;


import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@Repository
public class NodeStatusInMemoryRepository {

    private static final Map<String, Date> usersNotPlaying = new HashMap<>();


    public Date get(String sessionId) {
        return usersNotPlaying.get(sessionId);
    }

    public void insert(String sessionId, Date date) {
        usersNotPlaying.put(sessionId, date);
    }


}
