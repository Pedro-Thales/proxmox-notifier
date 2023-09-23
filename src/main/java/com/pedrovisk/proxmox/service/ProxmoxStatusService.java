package com.pedrovisk.proxmox.service;


import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.configuration.ThresholdProperties;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ProxmoxStatusService {
    private final ProxmoxApi proxmoxApi;

    private final ThresholdProperties thresholdProperties;

    public ProxmoxStatusService(ProxmoxApi proxmoxApi, ThresholdProperties thresholdProperties) {
        this.proxmoxApi = proxmoxApi;
        this.thresholdProperties = thresholdProperties;
    }

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-memory", name = "proxmox.get-memory-usage")
    public String getMemory() {

        var status = proxmoxApi.getNodeStatus();

        var memory = status.getData().getMemory();
        System.out.println("memory = " + memory);
        var percentUsed = BigDecimal.valueOf((memory.getUsed() / memory.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        System.out.println("Used memory: " + percentUsed + "%");
        var percentFree = BigDecimal.valueOf((memory.getFree() / memory.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        System.out.println("Free memory: " + percentFree + "%");

        //validate if the available memory is less than the threshold defined, if it is so notify via the webhook or something else

        return "Free memory: " + percentFree + "%";
    }

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-swap", name = "proxmox.get-swap-usage")
    public String getSwap() {

        var status = proxmoxApi.getNodeStatus();

        var swap = status.getData().getSwap();
        System.out.println("memory = " + swap);
        var percentUsed = BigDecimal.valueOf((swap.getUsed() / swap.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        System.out.println("Used swap: " + percentUsed + "%");
        var percentFree = BigDecimal.valueOf((swap.getFree() / swap.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        System.out.println("Free swap: " + percentFree + "%");

        return "Free swap: " + percentFree + "%";
    }

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-disk", name = "proxmox.get-disk-usage")
    public String getDisk() {

        var status = proxmoxApi.getNodeStatus();

        var disk = status.getData().getRootfs();
        System.out.println("memory = " + disk);
        var percentUsed = BigDecimal.valueOf((disk.getUsed() / disk.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        System.out.println("Used disk: " + percentUsed + "%");
        var percentFree = BigDecimal.valueOf((disk.getFree() / disk.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        System.out.println("Free disk: " + percentFree + "%");

        return "Free disk: " + percentFree + "%";
    }

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-node-status", name = "proxmox.get-node-status-usage")
    public void getNodeStatus() {

        var status = proxmoxApi.getNodeStatus();

        var memory = status.getData().getMemory();
        var freeMemoryPercent = BigDecimal.valueOf((memory.getFree() / memory.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);

        var swap = status.getData().getSwap();
        var freeSwapPercent = BigDecimal.valueOf((swap.getFree() / swap.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);

        var rootFs = status.getData().getRootfs();
        var freeRootFsPercent = BigDecimal.valueOf((rootFs.getFree() / rootFs.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);


        //TODO maybe use another threshold that points to this percentage to get warnings if is almost reaching that percentage
        // maybe to get levels of notifications (Warning, Dangerous)
        if (freeMemoryPercent.compareTo(BigDecimal.valueOf(thresholdProperties.freeMemory())) < 1) {
            //TODO Notify using the webhook, email and ntfy?
            //TODO use string template from java 21
            System.out.println("Free memory getting dangerous. Actual free: " + freeMemoryPercent + " Threshold: " + thresholdProperties.freeMemory());

        }

        if (freeSwapPercent.compareTo(BigDecimal.valueOf(thresholdProperties.freeSwap())) < 1) {
            //TODO Notify using the webhook, email and ntfy?
            //TODO use string template from java 21
            System.out.println("Free swap getting dangerous. Actual free: " + freeSwapPercent + " Threshold: " + thresholdProperties.freeSwap());

        }

        if (freeRootFsPercent.compareTo(BigDecimal.valueOf(thresholdProperties.freeRootfs())) < 1) {
            //TODO Notify using the webhook, email and ntfy?
            //TODO use string template from java 21
            System.out.println("Free rootfs getting dangerous. Actual free: " + freeRootFsPercent + " Threshold: " + thresholdProperties.freeRootfs());

        }


    }


}
