package com.pedrovisk.proxmox.service;


import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.configuration.ThresholdProperties;
import com.pedrovisk.proxmox.models.proxmox.ContainerStatus;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ContainersStatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainersStatusService.class);


    private final ProxmoxApi proxmoxApi;
    private final ThresholdProperties thresholdProperties;

    @MeasureRunTime
    public void getAllLxcStatus() {

        var status = proxmoxApi.getLxcContainersStatus();

        var lxcs = status.getData();
        for (ContainerStatus lxc : lxcs) {

            if ("stopped".equalsIgnoreCase(lxc.getStatus())) {
                continue;
            }

            var freeMemoryPercent = BigDecimal.valueOf(100 - (lxc.getMem() / lxc.getMaxmem()) * 100).setScale(2, RoundingMode.HALF_UP);
            var freeSwapPercent = BigDecimal.valueOf(100 - (lxc.getSwap() / lxc.getMaxswap()) * 100).setScale(2, RoundingMode.HALF_UP);
            var freeRootFsPercent = BigDecimal.valueOf(100 - (lxc.getDisk() / lxc.getMaxdisk()) * 100).setScale(2, RoundingMode.HALF_UP);


            //TODO maybe use another threshold that points to this percentage to get warnings if is almost reaching that percentage
            // maybe to get levels of notifications (Warning, Dangerous)
            // use specific threshold for lxcs
            if (freeMemoryPercent.compareTo(BigDecimal.valueOf(thresholdProperties.freeMemory())) < 1) {
                //TODO Notify using the webhook, email and ntfy?
                //TODO use string template from java 21
                LOGGER.info("Container " + lxc.getName() + " with free memory getting dangerous. Actual free: " + freeMemoryPercent + " Threshold: " + thresholdProperties.freeMemory());
            }

            if (freeSwapPercent.compareTo(BigDecimal.valueOf(thresholdProperties.freeSwap())) < 1) {
                LOGGER.info("Container " + lxc.getName() + " with free swap getting dangerous. Actual free: " + freeSwapPercent + " Threshold: " + thresholdProperties.freeSwap());
            }

            if (freeRootFsPercent.compareTo(BigDecimal.valueOf(thresholdProperties.freeRootfs())) < 1) {
                LOGGER.info("Container " + lxc.getName() + " with free rootfs getting dangerous. Actual free: " + freeRootFsPercent + " Threshold: " + thresholdProperties.freeRootfs());
            }


        }

    }

}
