package com.pedrovisk.proxmox.utils;

import com.pedrovisk.proxmox.models.ResourceUsedValuesDTO;
import com.pedrovisk.proxmox.models.json.ContainerConfiguration;
import com.pedrovisk.proxmox.models.notification.NotificationThreshold;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ResourceMapper {

    ContainerConfiguration containerConfiguration;
    ResourceUsedValuesDTO resourceUsedValuesDTO;
    String componentId;
    String componentType;

    public ResourceMapper(ContainerConfiguration containerConfiguration, ResourceUsedValuesDTO resourceUsedValuesDTO,
                          String componentId, String componentType) {
        this.containerConfiguration = containerConfiguration;
        this.resourceUsedValuesDTO = resourceUsedValuesDTO;
        this.componentId = componentId;
        this.componentType = componentType;

    }

    public List<NotificationThreshold> getAllValues() {
        return List.of(getCpuValue(), getMemoryValue(), getSwapValue(), getRootFsValue());
    }

    public NotificationThreshold getValue(BigDecimal actualValue, Integer threshold, String valueType) {
        return NotificationThreshold.builder().actualValue(actualValue).threshold(threshold)
                .componentId(componentId).valueType(valueType).componentType(componentType).build();
    }

    public NotificationThreshold getCpuValue() {
        return getValue(resourceUsedValuesDTO.usedCpuPercent,
                getValueOrOneHundred(containerConfiguration.usedCpuThreshold), "Cpu");
    }

    public NotificationThreshold getMemoryValue() {
        return getValue(resourceUsedValuesDTO.usedMemoryPercent,
                getValueOrOneHundred(containerConfiguration.usedMemoryThreshold), "Memory");
    }

    public NotificationThreshold getSwapValue() {
        return getValue(resourceUsedValuesDTO.usedSwapPercent,
                getValueOrOneHundred(containerConfiguration.usedSwapThreshold), "Swap");
    }

    public NotificationThreshold getRootFsValue() {
        return getValue(resourceUsedValuesDTO.usedRootFSPercent,
                getValueOrOneHundred(containerConfiguration.usedRootFSThreshold), "RootFS");
    }

    private int getValueOrOneHundred(Integer value) {
        return Optional.ofNullable(value).orElse(100);
    }


}
