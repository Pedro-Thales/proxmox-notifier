package com.pedrovisk.proxmox.service;

import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallLogData;
import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallLogRoot;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FirewallLogMonitorServiceTest {

    @Test
    public void test_logs_parsed_correctly() {
        // Mock the ProxmoxApi
        ProxmoxApi proxmoxApiMock = Mockito.mock(ProxmoxApi.class);

        // Create a FirewallLogRoot object with sample data
        FirewallLogRoot firewallLogRoot = new FirewallLogRoot();
        List<FirewallLogData> data = new ArrayList<>();
        FirewallLogData firewallLogData = new FirewallLogData(1, "0 6 PVEFW-HOST-IN 29/Sep/2023:21:37:18 -0300 DROP: IN=vmbr0 PHYSIN=enp6s0 MAC=ff:ff:ff:ff:ff:ff:a8:80:55:69:9d:0d:08:00 SRC=192.168.0.52 DST=255.255.255.255 LEN=200 TOS=0x00 PREC=0x00 TTL=255 ID=2606 PROTO=UDP SPT=59729 DPT=6667 LEN=180");
        data.add(firewallLogData);
        firewallLogRoot.setData(data);
        firewallLogRoot.setTotal(1);

        // Mock the getFirewallLog method to return the FirewallLogRoot object
        Mockito.when(proxmoxApiMock.getFirewallLog(Mockito.anyInt())).thenReturn(firewallLogRoot);

        // Create an instance of FirewallLogMonitorService with the mocked ProxmoxApi
        FirewallLogMonitorService firewallLogMonitorService = new FirewallLogMonitorService(proxmoxApiMock);

        // Call the getFirewallLogs method
        firewallLogMonitorService.getFirewallLogs();

        // Assert that the parsed log line is correct
        assertEquals(1, FirewallLogMonitorService.getLogCounter());
    }

}