package com.pedrovisk.proxmox.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

    //TODO do the error handling
    private final ProxmoxStatusService statusService;
    private final ContainersStatusService containersStatusService;
    private final VmStatusService vmStatusService;
    private final FirewallLogMonitorService firewallLogMonitorService;
    private final SshService sshService;

    @Scheduled(initialDelay = 3000, fixedDelayString = "${update.frequency.node-status}")
    public void statusScheduler() {

        //statusService.getNodeStatus();
        //containersStatusService.getAllLxcStatus();
        vmStatusService.getAllVmsStatus();

    }

    @Scheduled(initialDelay = 30000, fixedDelayString = "${update.frequency.firewall-log}")
    public void getFirewallLogsScheduler() throws InterruptedException {

        //firewallLogMonitorService.getFirewallLogs();

    }

    @Scheduled(initialDelay = 5000, fixedDelayString = "${update.frequency.temperature}")
    public void getTemperatureScheduler() throws Exception {

        //sshService.call();

    }
}
