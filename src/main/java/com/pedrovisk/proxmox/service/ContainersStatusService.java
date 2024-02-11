package com.pedrovisk.proxmox.service;


import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.models.NotificationDTO;
import com.pedrovisk.proxmox.models.json.RootConfiguration;
import com.pedrovisk.proxmox.service.notifications.NotificationSenderService;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContainersStatusService {

    private final ProxmoxApi proxmoxApi;
    private final RootConfiguration rootConfiguration;
    private final NotificationSenderService notificationSenderService;

    @MeasureRunTime
    public void getAllLxcStatus() {

        //TODO ADD CALL TO VMS 101 is not working
        for (var node : rootConfiguration.getNodes()) {
            for (var containerConfiguration : node.getContainers()) {
                var status = proxmoxApi.getLxcContainersStatus(node.getId(), String.valueOf(containerConfiguration.getId()));
                var lxc = status.getData();
                if ("stopped".equalsIgnoreCase(lxc.getStatus())) {
                    log.info("LXC STOPPED");
                    continue;
                }

                var usedMemoryPercent = getUsedPercent(lxc.getMem(), lxc.getMaxmem());
                var usedSwapPercent = getUsedPercent(lxc.getSwap(), lxc.getMaxswap());
                var usedRootFsPercent = getUsedPercent(lxc.getDisk(), lxc.getMaxdisk());

                var componentId = lxc.getVmid() + " - " + containerConfiguration.name;

                verifyThreshold(usedMemoryPercent,
                        BigDecimal.valueOf(containerConfiguration.usedMemoryThreshold), componentId, "Memory");
                verifyThreshold(usedSwapPercent,
                        BigDecimal.valueOf(containerConfiguration.usedSwapThreshold), componentId, "Swap");
                verifyThreshold(usedRootFsPercent,
                        BigDecimal.valueOf(containerConfiguration.usedRootFSThreshold), componentId, "RootFS");

            }
        }
    }

    private static BigDecimal getUsedPercent(double actual, double maxValue) {
        return BigDecimal.valueOf((actual / maxValue) * 100).setScale(2, RoundingMode.HALF_UP);
    }

    public void verifyThreshold(BigDecimal actualValue, BigDecimal memoryThreshold,
                                String componentId, String valueType) {
        var notificationDto = NotificationDTO.builder().actualValue(actualValue)
                .threshold(memoryThreshold).componentId(componentId)
                .valueType(valueType).componentType("LXC").build();

        if (notificationDto.actualValue().compareTo(notificationDto.threshold()) > -1) {
            notificationSenderService.sendNotification(notificationDto, null);
        }

    }


}
