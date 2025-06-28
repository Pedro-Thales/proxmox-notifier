package com.pedrovisk.proxmox.service;

import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.models.json.ContainerConfiguration;
import com.pedrovisk.proxmox.models.json.NodeConfiguration;
import com.pedrovisk.proxmox.models.json.RootConfiguration;
import com.pedrovisk.proxmox.models.proxmox.ContainerStatus;
import com.pedrovisk.proxmox.models.proxmox.LxcStatusRoot;
import com.pedrovisk.proxmox.repository.NotificationSentReminderInMemoryRepository;
import com.pedrovisk.proxmox.service.notifications.NotificationSender;
import com.pedrovisk.proxmox.service.notifications.NotificationSenderService;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;


class ContainersStatusServiceTest {


    // Retrieve LXC container status for all nodes
    @Test
    void test_retrieve_lxc_status_for_all_nodes() {
        // Arrange
        ProxmoxApi proxmoxApi = mock(ProxmoxApi.class);
        RootConfiguration rootConfiguration = mock(RootConfiguration.class);
        NotificationSenderService notificationSenderService = mock(NotificationSenderService.class);
        ContainersStatusService service = new ContainersStatusService(proxmoxApi, rootConfiguration, notificationSenderService);

        NodeConfiguration node = new NodeConfiguration();
        node.setId("node1");
        ContainerConfiguration container = new ContainerConfiguration();
        container.setId("1");
        container.setName("container1");
        container.setUsedCpuThreshold(80);
        container.setUsedMemoryThreshold(80);
        container.setUsedSwapThreshold(80);
        container.setUsedDiskThreshold(80);
        node.setContainers(List.of(container));
        when(rootConfiguration.getNodes()).thenReturn(List.of(node));

        LxcStatusRoot lxcStatusRoot = new LxcStatusRoot();
        ContainerStatus containerStatus = new ContainerStatus();
        containerStatus.setStatus("running");
        containerStatus.setMem(500);
        containerStatus.setMaxmem(1000);
        containerStatus.setSwap(200);
        containerStatus.setMaxswap(500);
        containerStatus.setDisk(300);
        containerStatus.setMaxdisk(1000);
        containerStatus.setCpu(0.5);
        lxcStatusRoot.setData(containerStatus);
        when(proxmoxApi.getLxcContainersStatus("node1", "1")).thenReturn(lxcStatusRoot);

        // Act
        service.getAllLxcStatus();

        // Assert
        verify(proxmoxApi).getLxcContainersStatus("node1", "1");
    }

    @Test
    void test_retrieve_lxc_status_for_all_nodes_integration_with_notification() {
        // Arrange
        ProxmoxApi proxmoxApi = mock(ProxmoxApi.class);
        RootConfiguration rootConfiguration = mock(RootConfiguration.class);

        NotificationSender mockNotifier = mock(NotificationSender.class);
        List<NotificationSender> notificators = List.of(mockNotifier);
        NotificationSentReminderInMemoryRepository repository = new NotificationSentReminderInMemoryRepository();
        NotificationSenderService notificationSenderService = new NotificationSenderService(notificators, repository);


        ContainersStatusService service = new ContainersStatusService(proxmoxApi, rootConfiguration, notificationSenderService);

        NodeConfiguration node = new NodeConfiguration();
        node.setId("node1");
        ContainerConfiguration container = new ContainerConfiguration();
        container.setId("1");
        container.setName("container1");
        container.setUsedCpuThreshold(80);
        container.setUsedMemoryThreshold(80);
        container.setUsedSwapThreshold(80);
        node.setContainers(List.of(container));
        when(rootConfiguration.getNodes()).thenReturn(List.of(node));

        LxcStatusRoot lxcStatusRoot = new LxcStatusRoot();
        ContainerStatus containerStatus = new ContainerStatus();
        containerStatus.setVmid("1");
        containerStatus.setStatus("running");
        containerStatus.setMem(500);
        containerStatus.setMaxmem(1000);
        containerStatus.setSwap(400);
        containerStatus.setMaxswap(500);
        containerStatus.setDisk(500);
        containerStatus.setMaxdisk(1000);
        containerStatus.setCpu(0.9);
        lxcStatusRoot.setData(containerStatus);
        when(proxmoxApi.getLxcContainersStatus("node1", "1")).thenReturn(lxcStatusRoot);

        // Act
        service.getAllLxcStatus();

        // Assert
        verify(proxmoxApi).getLxcContainersStatus("node1", "1");
        verify(mockNotifier, times(2)).sendNotification(any());

    }

    @Test
    void test_retrieve_lxc_status_for_all_nodes_returning_null_integration_with_notification() {
        // Arrange
        ProxmoxApi proxmoxApi = mock(ProxmoxApi.class);
        RootConfiguration rootConfiguration = mock(RootConfiguration.class);

        NotificationSender mockNotifier = mock(NotificationSender.class);
        List<NotificationSender> notificators = List.of(mockNotifier);
        NotificationSentReminderInMemoryRepository repository = new NotificationSentReminderInMemoryRepository();
        NotificationSenderService notificationSenderService = new NotificationSenderService(notificators, repository);


        ContainersStatusService service = new ContainersStatusService(proxmoxApi, rootConfiguration, notificationSenderService);

        NodeConfiguration node = new NodeConfiguration();
        node.setId("node1");
        ContainerConfiguration container = new ContainerConfiguration();
        container.setId("1");
        container.setName("container1");
        container.setUsedCpuThreshold(80);
        container.setUsedMemoryThreshold(80);
        container.setUsedSwapThreshold(80);
        node.setContainers(List.of(container));
        when(rootConfiguration.getNodes()).thenReturn(List.of(node));

        LxcStatusRoot lxcStatusRoot = new LxcStatusRoot();
        lxcStatusRoot.setData(null);
        when(proxmoxApi.getLxcContainersStatus("node1", "1")).thenReturn(lxcStatusRoot);

        // Act
        service.getAllLxcStatus();

        // Assert
        verify(proxmoxApi).getLxcContainersStatus("node1", "1");
        verify(mockNotifier, times(0)).sendNotification(any());

    }


    // Handle scenario where no nodes are configured
    @Test
    void test_no_nodes_configured() {
        // Arrange
        ProxmoxApi proxmoxApi = mock(ProxmoxApi.class);
        RootConfiguration rootConfiguration = mock(RootConfiguration.class);
        NotificationSenderService notificationSenderService = mock(NotificationSenderService.class);
        ContainersStatusService service = new ContainersStatusService(proxmoxApi, rootConfiguration, notificationSenderService);

        when(rootConfiguration.getNodes()).thenReturn(Collections.emptyList());

        // Act
        service.getAllLxcStatus();

        // Assert
        verify(proxmoxApi, never()).getLxcContainersStatus(anyString(), anyString());
    }

    // Handle scenario where no containers are configured for a node
    @Test
    void test_no_containers_configured_for_node() {
        // Arrange
        ProxmoxApi proxmoxApi = mock(ProxmoxApi.class);
        RootConfiguration rootConfiguration = mock(RootConfiguration.class);
        NotificationSenderService notificationSenderService = mock(NotificationSenderService.class);
        ContainersStatusService service = new ContainersStatusService(proxmoxApi, rootConfiguration, notificationSenderService);

        NodeConfiguration node = new NodeConfiguration();
        node.setId("node1");
        node.setContainers(Collections.emptyList());
        when(rootConfiguration.getNodes()).thenReturn(List.of(node));

        // Act
        service.getAllLxcStatus();

        // Assert
        verify(proxmoxApi, never()).getLxcContainersStatus(anyString(), anyString());
    }

}