package com.pedrovisk.proxmox.service;


import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.models.json.NodeConfiguration;
import com.pedrovisk.proxmox.models.json.RootConfiguration;
import com.pedrovisk.proxmox.models.json.VmsConfiguration;
import com.pedrovisk.proxmox.models.notification.NotificationBase;
import com.pedrovisk.proxmox.models.notification.NotificationError;
import com.pedrovisk.proxmox.models.notification.NotificationStoppedComponent;
import com.pedrovisk.proxmox.models.notification.NotificationThreshold;
import com.pedrovisk.proxmox.models.proxmox.vm.VmStatus;
import com.pedrovisk.proxmox.models.proxmox.vm.fs.VmFsStatusResult;
import com.pedrovisk.proxmox.service.notifications.NotificationSenderService;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VmStatusService {

    private final ProxmoxApi proxmoxApi;
    private final RootConfiguration rootConfiguration;
    private final NotificationSenderService notificationSenderService;

    @MeasureRunTime
    @Scheduled(initialDelay = 3000, fixedDelayString = "${update.frequency.node-status}")
    public void getAllVmsStatus() {

        List<NotificationBase> notifications = new ArrayList<>();

        for (var node : rootConfiguration.getNodes()) {
            for (var vmsConfiguration : node.getVms()) {

                var componentId = STR."\{vmsConfiguration.id}-\{vmsConfiguration.name}";
                var status = proxmoxApi.getVmStatus(node.getId(), String.valueOf(vmsConfiguration.getId()));
                var vmStatus = status.getData();

                if (vmStatus == null) {
                    var message = STR."The Proxmox api returned null for the vm: \{vmsConfiguration.name}";
                    notifications.add(NotificationError.builder().componentId(componentId).componentType("VM").message(message).build());
                    log.error(message);
                    continue;
                }
                if ("stopped".equalsIgnoreCase(vmStatus.getStatus())) {
                    notifications.add(NotificationStoppedComponent.builder().componentType("VM").componentId(componentId).build());
                    log.info("VM STOPPED");
                    continue;
                }


                if (vmsConfiguration.hasAgent) {
                    notifications.addAll(getAgentInfo(node, vmsConfiguration, componentId));
                }

                notifications.addAll(getNotificationsThresholdToSend(getResourceNotifications(vmStatus, vmsConfiguration)));

            }
        }

        sendNotifications(notifications);
    }

    private List<NotificationBase> getAgentInfo(NodeConfiguration node, VmsConfiguration vmsConfiguration, String componentId) {
        List<NotificationBase> agentNotifications = new ArrayList<>();
        var fsStatus = proxmoxApi.getVmFsStatus(node.getId(), String.valueOf(vmsConfiguration.getId()));

        if (fsStatus == null || fsStatus.getData() == null || fsStatus.getData().getResult().isEmpty()) {
            agentNotifications.add(NotificationError.builder().message(getErrorMessageForEmptyResponseFromAgent(componentId)).build());
            return agentNotifications;
        }

        for (VmFsStatusResult result : fsStatus.getData().getResult()) {
            var notification = NotificationThreshold.builder().actualValue(getUsedPercent(result.getUsedBytes(), result.getTotalBytes())).threshold(vmsConfiguration.usedDiskThreshold).componentId(componentId).componentType("VM").valueType(STR."Disk with mountpoint '\{result.getMountpoint()}'").build();

            if (actualValueIsGreaterOrEqualThreshold(notification)) {
                agentNotifications.add(notification);
            }
        }
        return agentNotifications;

    }

    private String getErrorMessageForEmptyResponseFromAgent(String componentId) {
        return STR.
                """
            VM: \{componentId} agent is set to true in the config.json file, but the return of the proxmox api was empty!

            Please check if the agent is installed and running in the VM.
            """;
    }


    private List<NotificationThreshold> getResourceNotifications(VmStatus vmStatus, VmsConfiguration vmsConfiguration) {

        var componentId = STR."\{vmsConfiguration.id}-\{vmsConfiguration.name}";

        return List.of(NotificationThreshold.builder().actualValue(getUsedPercent(vmStatus.getMem(), vmStatus.getMaxmem())).threshold(vmsConfiguration.usedMemoryThreshold).componentId(componentId).valueType("Memory").componentType("VM").build(), NotificationThreshold.builder().actualValue(BigDecimal.valueOf(vmStatus.getCpu() * 100)).threshold(vmsConfiguration.usedCpuThreshold).componentId(componentId).valueType("Cpu").componentType("VM").build());

    }

    private static BigDecimal getUsedPercent(double actual, double maxValue) {
        return BigDecimal.valueOf((actual / maxValue) * 100).setScale(2, RoundingMode.HALF_UP);
    }


    public void sendNotifications(List<NotificationBase> notifications) {

        for (NotificationBase notification : notifications) {
            notificationSenderService.sendNotification(notification);
        }

    }


    public List<NotificationThreshold> getNotificationsThresholdToSend(List<NotificationThreshold> notifications) {

        List<NotificationThreshold> list = new ArrayList<>();

        for (NotificationThreshold notification : notifications) {
            if (actualValueIsGreaterOrEqualThreshold(notification)) {
                list.add(notification);
            }
        }

        return list;
    }

    private static boolean actualValueIsGreaterOrEqualThreshold(NotificationThreshold notification) {
        return notification.getActualValue().compareTo(BigDecimal.valueOf(notification.getThreshold())) > -1;
    }


}
