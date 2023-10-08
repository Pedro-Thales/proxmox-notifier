package com.pedrovisk.proxmox.service;

import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.configuration.FirewallLogsProperties;
import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallLogData;
import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallLogRoot;
import com.pedrovisk.proxmox.repository.FirewallLogInMemoryRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FirewallLogMonitorServiceTest {


    @Before
    public void resetCount(){
        FirewallLogMonitorService.LOG_COUNTER = 0;
    }

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

        FirewallLogInMemoryRepository firewallLogRepository = new FirewallLogInMemoryRepository();

        // Mock the getFirewallLog method to return the FirewallLogRoot object
        Mockito.when(proxmoxApiMock.getFirewallLog(Mockito.anyInt())).thenReturn(firewallLogRoot);

        // Create an instance of FirewallLogMonitorService with the mocked ProxmoxApi
        FirewallLogsProperties firewallLogsProperties = new FirewallLogsProperties(true);
        FirewallLogMonitorService firewallLogMonitorService =
                new FirewallLogMonitorService(proxmoxApiMock, firewallLogRepository, firewallLogsProperties);

        // Call the getFirewallLogs method
        firewallLogMonitorService.getFirewallLogs();

        // Assert that the parsed log line is correct
        assertEquals(1, firewallLogRepository.getFirewallLogLines().size());
    }

    @Test
    public void test_multiple_logs_loading_parsed_correctly() {
        // Mock the ProxmoxApi
        ProxmoxApi proxmoxApiMock = Mockito.mock(ProxmoxApi.class);

        // Create a FirewallLogRoot object with sample data
        FirewallLogRoot firewallLogRoot = new FirewallLogRoot();
        List<FirewallLogData> data = getFirewallLogData();
        firewallLogRoot.setData(data);
        firewallLogRoot.setTotal(4);

        FirewallLogInMemoryRepository firewallLogRepository = new FirewallLogInMemoryRepository();

        // Mock the getFirewallLog method to return the FirewallLogRoot object
        Mockito.when(proxmoxApiMock.getFirewallLog(Mockito.anyInt())).thenReturn(firewallLogRoot);

        // Create an instance of FirewallLogMonitorService with the mocked ProxmoxApi
        FirewallLogsProperties firewallLogsProperties = new FirewallLogsProperties(true);
        FirewallLogMonitorService firewallLogMonitorService =
                new FirewallLogMonitorService(proxmoxApiMock, firewallLogRepository, firewallLogsProperties);

        // Call the getFirewallLogs method
        firewallLogMonitorService.getFirewallLogs();
        // Assert that the parsed log line is correct
        assertEquals(4, firewallLogRepository.getFirewallLogLines().size());


    }

    @Test
    public void test_multiple_logs_not_loading_parsed_correctly() {
        // Mock the ProxmoxApi
        ProxmoxApi proxmoxApiMock = Mockito.mock(ProxmoxApi.class);

        // Create a FirewallLogRoot object with sample data
        FirewallLogRoot firewallLogRoot = new FirewallLogRoot();
        List<FirewallLogData> data = getFirewallLogData();
        firewallLogRoot.setData(data);
        firewallLogRoot.setTotal(4);

        FirewallLogInMemoryRepository firewallLogRepository = new FirewallLogInMemoryRepository();

        // Mock the getFirewallLog method to return the FirewallLogRoot object
        Mockito.when(proxmoxApiMock.getFirewallLog(Mockito.anyInt())).thenReturn(firewallLogRoot);

        // Create an instance of FirewallLogMonitorService with the mocked ProxmoxApi
        FirewallLogsProperties firewallLogsProperties = new FirewallLogsProperties(false);
        FirewallLogMonitorService firewallLogMonitorService =
                new FirewallLogMonitorService(proxmoxApiMock, firewallLogRepository, firewallLogsProperties);

        // Call the getFirewallLogs method
        firewallLogMonitorService.getFirewallLogs();

        // Assert that the parsed log line is correct
        assertEquals(1, firewallLogRepository.getFirewallLogLines().size());
    }

    @NotNull
    private static List<FirewallLogData> getFirewallLogData() {
        FirewallLogData firewallLogData = new FirewallLogData(1, "0 6 PVEFW-HOST-IN 29/Sep/2023:21:37:18 -0300 DROP: IN=vmbr0 PHYSIN=enp6s0 MAC=ff:ff:ff:ff:ff:ff:a8:80:55:69:9d:0d:08:00 SRC=192.168.0.52 DST=255.255.255.255 LEN=200 TOS=0x00 PREC=0x00 TTL=255 ID=2606 PROTO=UDP SPT=59729 DPT=6667 LEN=180");
        FirewallLogData firewallLogData2 = new FirewallLogData(2, "0 4 PVEFW-HOST-IN 03/Oct/2023:12:27:00 -0300 ACCEPT: IN=vmbr0 PHYSIN=enp6s0 MAC=22:33:4d:05:0d:7b:4c:44:5b:84:08:c3:08:00 SRC=192.168.0.130 DST=192.168.0.10 LEN=52 TOS=0x00 PREC=0x00 TTL=128 ID=3796 DF PROTO=TCP SPT=58991 DPT=8006 SEQ=3006448406 ACK=0 WINDOW=64240 SYN ");
        FirewallLogData firewallLogData3 = new FirewallLogData(3, "100 6 veth100i0-IN 26/Sep/2023:16:49:52 -0300 ACCEPT: IN=fwbr100i0 OUT=fwbr100i0 PHYSIN=fwln100i0 PHYSOUT=veth100i0 MAC=b2:d7:d4:50:32:67:4c:44:5b:84:08:c3:08:00 SRC=192.168.0.130 DST=192.168.0.15 LEN=52 TOS=0x00 PREC=0x00 TTL=128 ID=3178 DF PROTO=TCP SPT=62620 DPT=8096 SEQ=119646031 ACK=0 WINDOW=64240 SYN ");
        FirewallLogData firewallLogData4 = new FirewallLogData(4, "0 6 PVEFW-HOST-IN 29/Sep/2023:21:47:18 -0300 DROP: IN=vmbr0 PHYSIN=enp6s0 MAC=ff:ff:ff:ff:ff:ff:a8:80:55:69:9d:0d:08:00 SRC=192.168.0.52 DST=255.255.255.255 LEN=200 TOS=0x00 PREC=0x00 TTL=255 ID=2606 PROTO=UDP SPT=59729 DPT=6667 LEN=180");
        List<FirewallLogData> data = new ArrayList<>(List.of(firewallLogData, firewallLogData2, firewallLogData3, firewallLogData4));
        return data;
    }

    @NotNull
    private static List<FirewallLogData> getFirewallLogData3() {
        FirewallLogData firewallLogData = new FirewallLogData(9, "0 6 PVEFW-HOST-IN 29/Sep/2023:21:37:18 -0300 DROP: IN=vmbr0 PHYSIN=enp6s0 MAC=ff:ff:ff:ff:ff:ff:a8:80:55:69:9d:0d:08:00 SRC=192.168.0.52 DST=255.255.255.255 LEN=200 TOS=0x00 PREC=0x00 TTL=255 ID=2606 PROTO=UDP SPT=59729 DPT=6667 LEN=180");
        FirewallLogData firewallLogData2 = new FirewallLogData(10, "0 4 PVEFW-HOST-IN 03/Oct/2023:12:27:00 -0300 ACCEPT: IN=vmbr0 PHYSIN=enp6s0 MAC=22:33:4d:05:0d:7b:4c:44:5b:84:08:c3:08:00 SRC=192.168.0.130 DST=192.168.0.10 LEN=52 TOS=0x00 PREC=0x00 TTL=128 ID=3796 DF PROTO=TCP SPT=58991 DPT=8006 SEQ=3006448406 ACK=0 WINDOW=64240 SYN ");
        FirewallLogData firewallLogData3 = new FirewallLogData(11, "100 6 veth100i0-IN 26/Sep/2023:16:49:52 -0300 ACCEPT: IN=fwbr100i0 OUT=fwbr100i0 PHYSIN=fwln100i0 PHYSOUT=veth100i0 MAC=b2:d7:d4:50:32:67:4c:44:5b:84:08:c3:08:00 SRC=192.168.0.130 DST=192.168.0.15 LEN=52 TOS=0x00 PREC=0x00 TTL=128 ID=3178 DF PROTO=TCP SPT=62620 DPT=8096 SEQ=119646031 ACK=0 WINDOW=64240 SYN ");
        FirewallLogData firewallLogData4 = new FirewallLogData(12, "0 6 PVEFW-HOST-IN 29/Sep/2023:21:47:18 -0300 DROP: IN=vmbr0 PHYSIN=enp6s0 MAC=ff:ff:ff:ff:ff:ff:a8:80:55:69:9d:0d:08:00 SRC=192.168.0.52 DST=255.255.255.255 LEN=200 TOS=0x00 PREC=0x00 TTL=255 ID=2606 PROTO=UDP SPT=59729 DPT=6667 LEN=180");
        List<FirewallLogData> data = new ArrayList<>(List.of(firewallLogData, firewallLogData2, firewallLogData3, firewallLogData4));
        return data;
    }

}