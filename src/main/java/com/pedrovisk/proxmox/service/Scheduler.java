package com.pedrovisk.proxmox.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    @Autowired
    ProxmoxStatusService statusService;

    @Scheduled(fixedDelayString = "${update.frequency.node-status}")
    public void shutdownJob() {

        statusService.getNodeStatus();

    }
}
