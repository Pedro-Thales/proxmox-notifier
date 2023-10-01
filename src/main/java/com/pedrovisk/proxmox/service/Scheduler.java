package com.pedrovisk.proxmox.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    @Autowired
    ProxmoxStatusService statusService;

    @Autowired
    ContainersStatusService containersStatusService;

    @Autowired
    FirewallLogMonitorService firewallLogMonitorService;

    //@Scheduled(fixedDelayString = "${update.frequency.node-status}")
    public void statusScheduler() {

        statusService.getNodeStatus();
        containersStatusService.getAllLxcStatus();

    }

    @Scheduled(fixedDelayString = "${update.frequency.firewall-log}")
    public void getFirewallLogsScheduler() throws InterruptedException {

        firewallLogMonitorService.getFirewallLogs();

    }
}
