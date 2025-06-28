package com.pedrovisk.proxmox.service;


import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.models.ResourceUsedValuesDTO;
import com.pedrovisk.proxmox.models.json.ContainerConfiguration;
import com.pedrovisk.proxmox.models.json.RootConfiguration;
import com.pedrovisk.proxmox.models.notification.NotificationThreshold;
import com.pedrovisk.proxmox.models.proxmox.ContainerStatus;
import com.pedrovisk.proxmox.service.notifications.NotificationSenderService;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import com.pedrovisk.proxmox.utils.ResourceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContainersStatusService {

    private final ProxmoxApi proxmoxApi;
    private final RootConfiguration rootConfiguration;
    private final NotificationSenderService notificationSenderService;

    @MeasureRunTime
    @Scheduled(initialDelay = 3000, fixedDelayString = "${update.frequency.node-status}")
    public void getAllLxcStatus() {

        //TODO Manage time between notifications and add control for schedule in the config.json?
        for (var node : rootConfiguration.getNodes()) {
            for (var containerConfiguration : node.getContainers()) {
                try {
                    var status = proxmoxApi.getLxcContainersStatus(node.getId(), String.valueOf(containerConfiguration.getId()));
                    if (status != null && status.getData() != null) {
                        processLxcStatus(containerConfiguration, status.getData());
                    } else {
                        log.error("Received null for LXC: {}", containerConfiguration.getName());
                    }
                } catch (Exception e) {
                    log.error("Error getting LXC status for container: {}", containerConfiguration.getName(), e);
                }

            }
        }
    }

    private void processLxcStatus(ContainerConfiguration containerConfiguration, ContainerStatus lxc) {
        if ("stopped".equalsIgnoreCase(lxc.getStatus())) {
            log.info("LXC stopped: {}", containerConfiguration.getName());
            //TODO notify if stopped
            return;
        }

        var resourceUsedValuesDTO = calculateResourceValues(lxc);
        var componentId = String.format("%s - %s", lxc.getVmid(), containerConfiguration.getName());

        var resourceMapper = new ResourceMapper(containerConfiguration, resourceUsedValuesDTO, componentId, "LXC");
        sendNotificationsIfThreshold(resourceMapper.getAllValues());
    }

    private ResourceUsedValuesDTO calculateResourceValues(ContainerStatus lxc) {
        ResourceUsedValuesDTO resourceUsedValuesDTO = new ResourceUsedValuesDTO();
        resourceUsedValuesDTO.usedMemoryPercent = getUsedPercent(lxc.getMem(), lxc.getMaxmem());
        resourceUsedValuesDTO.usedSwapPercent = getUsedPercent(lxc.getSwap(), lxc.getMaxswap());
        resourceUsedValuesDTO.usedDiskPercent = getUsedPercent(lxc.getDisk(), lxc.getMaxdisk());
        resourceUsedValuesDTO.usedCpuPercent = BigDecimal.valueOf(lxc.getCpu() * 100);

        return resourceUsedValuesDTO;
    }

    private static BigDecimal getUsedPercent(double actual, double maxValue) {
        if (maxValue <= 0) {
            return BigDecimal.ZERO; // Prevent division by zero
        }
        return BigDecimal.valueOf((actual / maxValue) * 100).setScale(2, RoundingMode.HALF_UP);
    }

    public void sendNotificationsIfThreshold(List<NotificationThreshold> notifications) {

        for (NotificationThreshold notification : notifications) {
            if (notification.getActualValue().compareTo(BigDecimal.valueOf(notification.getThreshold())) > -1) {
                notificationSenderService.sendNotification(notification);
            }
        }
    }


}
