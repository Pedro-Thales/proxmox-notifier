package com.pedrovisk.proxmox.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.pedrovisk.proxmox.WiremockConfig;
import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.models.json.NodeConfiguration;
import com.pedrovisk.proxmox.models.json.RootConfiguration;
import com.pedrovisk.proxmox.models.json.VmsConfiguration;
import com.pedrovisk.proxmox.models.notification.NotificationBase;
import com.pedrovisk.proxmox.models.notification.NotificationError;
import com.pedrovisk.proxmox.models.notification.NotificationThreshold;
import com.pedrovisk.proxmox.service.notifications.NotificationSenderService;
import nl.altindag.ssl.util.internal.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;

import static java.nio.charset.Charset.defaultCharset;
import static org.mockito.Mockito.*;
import static org.springframework.util.StreamUtils.copyToString;


@SpringBootTest
@ActiveProfiles("test")
@EnableFeignClients
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WiremockConfig.class })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VmStatusServiceIntegrationTest {


    @Autowired
    private WireMockServer mockProxmoxService;

    @MockBean
    private ProxmoxStatusService proxmoxStatusService;
    @MockBean
    private ContainersStatusService containersStatusService;
    @MockBean
    private FirewallLogMonitorService firewallLogMonitorService;
    @MockBean
    private Scheduler scheduler;
    @MockBean
    private SshService sshService;
    @MockBean
    private RootConfiguration rootConfiguration;
    @MockBean
    private NotificationSenderService notificationSenderService;

    @Autowired
    private VmStatusService vmStatusService;

    @SpyBean
    private ProxmoxApi proxmoxApi;


    void setupWireMockProxmoxResponse(String url, String responseJsonPath) throws IOException {
        mockProxmoxService.stubFor(WireMock.get(WireMock.urlEqualTo(url))
                .willReturn(WireMock.aResponse()
                                    .withStatus(HttpStatus.OK.value())
                                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                    .withBody(copyToString(IOUtils.getResourceAsStream(responseJsonPath),
                                            defaultCharset()))));

    }


    @Test
    void testGetAllVmsWithTwoVmsWithAgent_returningOk() throws IOException {

        ArgumentCaptor<NotificationBase> captor = ArgumentCaptor.forClass(NotificationBase.class);

        setupWireMockProxmoxResponse("/nodes/node1/qemu/1/status/current", "responses/vm-status-response-ok.json");
        setupWireMockProxmoxResponse("/nodes/node1/qemu/1/agent/get-fsinfo", "responses/vm-fs-status-response-ok.json");
        setupWireMockProxmoxResponse("/nodes/node1/qemu/2/status/current", "responses/vm-status-response-ok.json");


        NodeConfiguration node = new NodeConfiguration();
        node.setId("node1");
        var vmsConfigurations = List.of(
                VmsConfiguration.builder().id(1).name("container1")
                        .usedCpuThreshold(80).usedMemoryThreshold(10).usedSwapThreshold(80).usedDiskThreshold(10)
                        .hasAgent(true)
                        .build(),
                VmsConfiguration.builder().id(2).name("container2")
                        .usedCpuThreshold(90).usedMemoryThreshold(10).usedSwapThreshold(90)
                        .hasAgent(false)
                        .build()
                );
        node.setVms(vmsConfigurations);

        when(rootConfiguration.getNodes()).thenReturn(List.of(node));

        Assertions.assertDoesNotThrow(() -> vmStatusService.getAllVmsStatus());
        verify(proxmoxApi, times(1)).getVmStatus("node1", "1");
        verify(proxmoxApi, times(1)).getVmFsStatus("node1", "1");
        verify(proxmoxApi, times(1)).getVmStatus("node1", "2");
        verify(proxmoxApi, never()).getVmFsStatus("node1", "2");
        verify(notificationSenderService, times(3)).sendNotification(captor.capture());

        Assertions.assertEquals(captor.getAllValues().get(0).getClass(), NotificationThreshold.class);
        Assertions.assertEquals(captor.getAllValues().get(1).getClass(), NotificationThreshold.class);
        Assertions.assertEquals(captor.getAllValues().get(2).getClass(), NotificationThreshold.class);

    }

    @Test
    void testGetAllVmsWithTwoVmsWithAgent_returningEmptyForTheSecondVmStatus() throws IOException {

        ArgumentCaptor<NotificationBase> captor = ArgumentCaptor.forClass(NotificationBase.class);

        setupWireMockProxmoxResponse("/nodes/node1/qemu/1/status/current", "responses/vm-status-response-ok.json");
        setupWireMockProxmoxResponse("/nodes/node1/qemu/1/agent/get-fsinfo", "responses/vm-fs-status-response-ok.json");
        setupWireMockProxmoxResponse("/nodes/node1/qemu/2/status/current", "responses/vm-not-exists-response-null.json");

        NodeConfiguration node = new NodeConfiguration();
        node.setId("node1");
        var vmsConfigurations = List.of(
                VmsConfiguration.builder().id(1).name("container1")
                        .usedCpuThreshold(80).usedMemoryThreshold(10).usedSwapThreshold(80).usedDiskThreshold(10)
                        .hasAgent(true)
                        .build(),
                VmsConfiguration.builder().id(2).name("container2")
                        .usedCpuThreshold(90).usedMemoryThreshold(10).usedSwapThreshold(90)
                        .hasAgent(true)
                        .build()
        );
        node.setVms(vmsConfigurations);

        when(rootConfiguration.getNodes()).thenReturn(List.of(node));

        Assertions.assertDoesNotThrow(() -> vmStatusService.getAllVmsStatus());

        verify(proxmoxApi, times(1)).getVmStatus("node1", "1");
        verify(proxmoxApi, times(1)).getVmFsStatus("node1", "1");
        verify(proxmoxApi, times(1)).getVmStatus("node1", "2");
        verify(proxmoxApi, never()).getVmFsStatus("node1", "2");
        verify(notificationSenderService, times(3)).sendNotification(captor.capture());

        var errorMessage = "The Proxmox api returned null for the vm: container2";

        Assertions.assertEquals(captor.getAllValues().get(0).getClass(), NotificationThreshold.class);
        Assertions.assertEquals(captor.getAllValues().get(1).getClass(), NotificationThreshold.class);
        Assertions.assertEquals(captor.getAllValues().get(2).getClass(), NotificationError.class);
        Assertions.assertEquals(captor.getAllValues().get(2).getMessage(), errorMessage);

    }

    @Test
    void testGetAllVmsWithTwoVmsWithAgent_returningEmptyForTheSecondVmFsStatus() throws IOException {

        ArgumentCaptor<NotificationBase> captor = ArgumentCaptor.forClass(NotificationBase.class);

        setupWireMockProxmoxResponse("/nodes/node1/qemu/1/status/current", "responses/vm-status-response-ok.json");
        setupWireMockProxmoxResponse("/nodes/node1/qemu/1/agent/get-fsinfo", "responses/vm-fs-status-response-ok.json");
        setupWireMockProxmoxResponse("/nodes/node1/qemu/2/status/current", "responses/vm-status-response-ok.json");
        setupWireMockProxmoxResponse("/nodes/node1/qemu/2/agent/get-fsinfo", "responses/vm-not-exists-response-null.json");

        NodeConfiguration node = new NodeConfiguration();
        node.setId("node1");
        var vmsConfigurations = List.of(
                VmsConfiguration.builder().id(1).name("container1")
                        .usedCpuThreshold(80).usedMemoryThreshold(10).usedSwapThreshold(80).usedDiskThreshold(10)
                        .hasAgent(true)
                        .build(),
                VmsConfiguration.builder().id(2).name("container2")
                        .usedCpuThreshold(90).usedMemoryThreshold(10).usedSwapThreshold(90)
                        .hasAgent(true)
                        .build()
        );
        node.setVms(vmsConfigurations);

        when(rootConfiguration.getNodes()).thenReturn(List.of(node));

        Assertions.assertDoesNotThrow(() -> vmStatusService.getAllVmsStatus());

        verify(proxmoxApi, times(1)).getVmStatus("node1", "1");
        verify(proxmoxApi, times(1)).getVmFsStatus("node1", "1");
        verify(proxmoxApi, times(1)).getVmStatus("node1", "2");
        verify(proxmoxApi, times(1)).getVmFsStatus("node1", "2");
        verify(notificationSenderService, times(4)).sendNotification(captor.capture());


        var errorMessage = STR."""
                VM: 2-container2 agent is set to true in the config.json file, but the return of the proxmox api was empty!
                
                Please check if the agent is installed and running in the VM.
                """;

        Assertions.assertEquals(captor.getAllValues().get(0).getClass(), NotificationThreshold.class);
        Assertions.assertEquals(captor.getAllValues().get(1).getClass(), NotificationThreshold.class);
        Assertions.assertEquals(captor.getAllValues().get(2).getClass(), NotificationError.class);
        Assertions.assertEquals(captor.getAllValues().get(2).getMessage(), errorMessage);
        Assertions.assertEquals(captor.getAllValues().get(3).getClass(), NotificationThreshold.class);

    }


}