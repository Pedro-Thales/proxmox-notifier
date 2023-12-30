package com.pedrovisk.proxmox.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    //TODO do the error handling

    @Autowired
    ProxmoxStatusService statusService;
    @Autowired
    ContainersStatusService containersStatusService;
    @Autowired
    FirewallLogMonitorService firewallLogMonitorService;
    @Autowired
    SshService sshService;

    @Scheduled(initialDelay = 120000, fixedDelayString = "${update.frequency.node-status}")
    public void statusScheduler() {

        //statusService.getNodeStatus();
        //containersStatusService.getAllLxcStatus();

    }

    @Scheduled(initialDelay = 30000, fixedDelayString = "${update.frequency.firewall-log}")
    public void getFirewallLogsScheduler() throws InterruptedException {

        //firewallLogMonitorService.getFirewallLogs();

    }

    @Scheduled(initialDelay = 5000, fixedDelayString = "${update.frequency.temperature}")
    public void getTemperatureScheduler() throws Exception {

        sshService.call();

    }
}
