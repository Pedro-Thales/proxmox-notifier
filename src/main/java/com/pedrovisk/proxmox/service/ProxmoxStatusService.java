package com.pedrovisk.proxmox.service;


import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.configuration.ThresholdProperties;
import com.pedrovisk.proxmox.models.NotificationDTO;
import com.pedrovisk.proxmox.models.json.NodeConfiguration;
import com.pedrovisk.proxmox.models.json.RootConfiguration;
import com.pedrovisk.proxmox.service.notifications.NotificationSenderService;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
        var percentUsed = memory.getUsed().divide(memory.getTotal(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        log.info("Used memory: " + percentUsed + "%");
        var percentFree = memory.getFree().divide(memory.getTotal(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
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

        for (NodeConfiguration node : rootConfiguration.getNodes()) {

            var status = proxmoxApi.getNodeStatus(node.getId());

            var memory = status.getData().getMemory();
            var freeMemoryPercent = memory.getFree().divide(memory.getTotal(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);

            var usedMemoryPercent = memory.getUsed().divide(memory.getTotal(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);

            var swap = status.getData().getSwap();
            var freeSwapPercent = BigDecimal.valueOf((swap.getFree() / swap.getTotal()) * 100)
                    .setScale(2, RoundingMode.HALF_UP);

            var rootFs = status.getData().getRootfs();
            var freeRootFsPercent = BigDecimal.valueOf((rootFs.getFree() / rootFs.getTotal()) * 100)
                    .setScale(2, RoundingMode.HALF_UP);

            // Maybe include in this map a value of delay time to get this metric
            if (freeMemoryPercent.compareTo(BigDecimal.valueOf(thresholdProperties.freeMemory())) < 1) {
                //TODO Notify using the webhook - discord? telegram? whatsapp?
                // ntfy - https://docs.ntfy.sh/config/  - smtp config too? Flag to enable or disable smtp from ntfy or disable everything


                log.info(STR.
                        "Free memory getting dangerous. Actual free: \{freeMemoryPercent} Threshold: \{thresholdProperties.freeMemory()}");

                notificationService.sendNotification(NotificationDTO.builder().build(), null);

            }

            if (freeSwapPercent.compareTo(BigDecimal.valueOf(thresholdProperties.freeSwap())) < 1) {
                //TODO Notify using the webhook, email and ntfy?
                //TODO use string template from java 21
                log.info("Free swap getting dangerous. Actual free: " + freeSwapPercent + " Threshold: " + thresholdProperties.freeSwap());

            }

            if (freeRootFsPercent.compareTo(BigDecimal.valueOf(thresholdProperties.freeRootfs())) < 1) {
                //TODO Notify using the webhook, email and ntfy?
                //TODO use string template from java 21
                log.info("Free rootfs getting dangerous. Actual free: " + freeRootFsPercent + " Threshold: " + thresholdProperties.freeRootfs());

            }


        }



    }


}
