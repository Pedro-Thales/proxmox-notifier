package com.pedrovisk.proxmox.service;


import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.models.ResourceUsedValuesDTO;
import com.pedrovisk.proxmox.models.json.RootConfiguration;
import com.pedrovisk.proxmox.models.notification.NotificationThreshold;
import com.pedrovisk.proxmox.models.proxmox.ContainerStatus;
import com.pedrovisk.proxmox.service.notifications.NotificationSenderService;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import com.pedrovisk.proxmox.utils.ResourceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void getAllLxcStatus() {

        //TODO Manage time between notifications and add control for schedule in the config.json?
        for (var node : rootConfiguration.getNodes()) {
            for (var containerConfiguration : node.getContainers()) {
                var status = proxmoxApi.getLxcContainersStatus(node.getId(), String.valueOf(containerConfiguration.getId()));
                //TODO Validate all results from apis if this is null is crashing, what about other fields?
                var lxc = status.getData();

                if (lxc == null) {
                    log.error("The Proxmox api returned null for the lxc: {}", containerConfiguration.name);
                    continue;
                }

                if ("stopped".equalsIgnoreCase(lxc.getStatus())) {
                    log.info("LXC STOPPED");
                    //TODO notify if stopped
                    continue;
                }

                var resourceUsedValuesDTO = getResourceValues(lxc);
                var componentId = STR."\{lxc.getVmid()} - \{containerConfiguration.name}";
                //TODO refactor ResourceMapper to not use it or make it generic to be used in other places,
                // get rid of containerConfiguration?
                var resourceMapper = new ResourceMapper(containerConfiguration, resourceUsedValuesDTO, componentId, "LXC");
                var notifications = resourceMapper.getAllValues();

                sendNotificationsIfThreshold(notifications);

            }
        }
    }

    private ResourceUsedValuesDTO getResourceValues(ContainerStatus lxc) {
        ResourceUsedValuesDTO resourceUsedValuesDTO = new ResourceUsedValuesDTO();
        resourceUsedValuesDTO.usedMemoryPercent = getUsedPercent(lxc.getMem(), lxc.getMaxmem());
        resourceUsedValuesDTO.usedSwapPercent = getUsedPercent(lxc.getSwap(), lxc.getMaxswap());
        resourceUsedValuesDTO.usedDiskPercent = getUsedPercent(lxc.getDisk(), lxc.getMaxdisk());
        resourceUsedValuesDTO.usedCpuPercent = BigDecimal.valueOf(lxc.getCpu() * 100);

        return resourceUsedValuesDTO;
    }

    private static BigDecimal getUsedPercent(double actual, double maxValue) {
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
