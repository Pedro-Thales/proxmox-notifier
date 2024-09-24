package com.pedrovisk.proxmox.service;


import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.configuration.ThresholdProperties;
import com.pedrovisk.proxmox.models.ResourceUsedValuesDTO;
import com.pedrovisk.proxmox.models.json.NodeConfiguration;
import com.pedrovisk.proxmox.models.json.RootConfiguration;
import com.pedrovisk.proxmox.models.notification.NotificationThreshold;
import com.pedrovisk.proxmox.models.proxmox.NodeStatus;
import com.pedrovisk.proxmox.service.notifications.NotificationSenderService;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import com.pedrovisk.proxmox.utils.ResourceMapper;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProxmoxStatusService {

    private final NotificationSenderService notificationService;
    private final ProxmoxApi proxmoxApi;
    private final ThresholdProperties thresholdProperties;
    private final RootConfiguration rootConfiguration;

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-memory", name = "proxmox.get-memory-usage")
    public String getMemory() {

        var status = proxmoxApi.getNodeStatus(rootConfiguration.getNodes().getFirst().getId());

        var memory = status.getData().getMemory();
        log.info("memory = " + memory);
        var percentUsed = memory.getUsed().divide(memory.getTotal(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        log.info("Used memory: " + percentUsed + "%");
        var percentFree = memory.getFree().divide(memory.getTotal(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        log.info("Free memory: " + percentFree + "%");

        //validate if the available memory is less than the threshold defined, if it is so notify via the webhook or something else
        //notificationService.sendNotification(NotificationDTO.builder().build(), null);

        return "Free memory: " + percentFree + "%";
    }

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-swap", name = "proxmox.get-swap-usage")
    public String getSwap() {

        var status = proxmoxApi.getNodeStatus(rootConfiguration.getNodes().getFirst().getId());


        var swap = status.getData().getSwap();
        log.info("memory = " + swap);
        log.info("memory = " + swap);
        var percentUsed = BigDecimal.valueOf((swap.getUsed() / swap.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        log.info("Used swap: " + percentUsed + "%");
        var percentFree = BigDecimal.valueOf((swap.getFree() / swap.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        log.info("Free swap: " + percentFree + "%");

        return "Free swap: " + percentFree + "%";
    }

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-disk", name = "proxmox.get-disk-usage")
    public String getDisk() {

        var status = proxmoxApi.getNodeStatus(rootConfiguration.getNodes().getFirst().getId());

        var disk = status.getData().getRootfs();
        log.info("memory = " + disk);
        var percentUsed = BigDecimal.valueOf((disk.getUsed() / disk.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        log.info("Used disk: " + percentUsed + "%");
        var percentFree = BigDecimal.valueOf((disk.getFree() / disk.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        log.info("Free disk: " + percentFree + "%");

        return "Free disk: " + percentFree + "%";
    }

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-node-status", name = "proxmox.get-node-status-usage")
    public void getNodeStatus() {

        for (NodeConfiguration nodeConfiguration : rootConfiguration.getNodes()) {

            var status = proxmoxApi.getNodeStatus(nodeConfiguration.getId());

            var nodeStatus = status.getData();

            if (nodeStatus == null) {
                log.error("The Proxmox api returned null for the node: {}", nodeConfiguration.name);
                continue;
            }

            var resourceUsedValuesDTO = getResourceValues(nodeStatus);
            var componentId = STR."\{nodeConfiguration.getId()} - \{nodeConfiguration.getName()}";
            //TODO refactor ResourceMapper to not use it or make it generic to be used in other places,
            // get rid of containerConfiguration?
            var resourceMapper = new ResourceMapper(nodeConfiguration, resourceUsedValuesDTO, componentId, "LXC");
            var notifications = resourceMapper.getAllValues();

            sendNotificationsIfThreshold(notifications);


        }


    }

    private ResourceUsedValuesDTO getResourceValues(NodeStatus nodeStatus) {
        ResourceUsedValuesDTO resourceUsedValuesDTO = new ResourceUsedValuesDTO();
        resourceUsedValuesDTO.usedMemoryPercent = getUsedPercent(
                nodeStatus.getMemory().getUsed(), nodeStatus.getMemory().getTotal());
        resourceUsedValuesDTO.usedSwapPercent = getUsedPercent(
                nodeStatus.getSwap().getUsed(), nodeStatus.getSwap().getTotal());
        resourceUsedValuesDTO.usedDiskPercent = getUsedPercent(
                nodeStatus.getRootfs().getUsed(), nodeStatus.getRootfs().getTotal());

        resourceUsedValuesDTO.usedCpuPercent = BigDecimal.valueOf(nodeStatus.getCpu() * 100);

        return resourceUsedValuesDTO;
    }

    private static BigDecimal getUsedPercent(BigDecimal actual, BigDecimal maxValue) {
        return getUsedPercent(actual.doubleValue(), maxValue.doubleValue());
    }

    private static BigDecimal getUsedPercent(double actual, double maxValue) {
        return BigDecimal.valueOf((actual / maxValue) * 100).setScale(2, RoundingMode.HALF_UP);
    }

    public void sendNotificationsIfThreshold(List<NotificationThreshold> notifications) {

        for (NotificationThreshold notification : notifications) {
            if (notification.getActualValue().compareTo(BigDecimal.valueOf(notification.getThreshold())) > -1) {
                notificationService.sendNotification(notification);
            }
        }
    }




}
