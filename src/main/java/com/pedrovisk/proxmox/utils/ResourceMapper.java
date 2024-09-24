package com.pedrovisk.proxmox.utils;

import com.pedrovisk.proxmox.models.ResourceUsedValuesDTO;
import com.pedrovisk.proxmox.models.json.ConfigurationBase;
import com.pedrovisk.proxmox.models.notification.NotificationThreshold;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ResourceMapper {

    ResourceUsedValuesDTO resourceUsedValuesDTO;
    String componentId;
    String componentType;
    ConfigurationBase configurationBase;


    public ResourceMapper(ConfigurationBase configuration, ResourceUsedValuesDTO resourceUsedValuesDTO,
                          String componentId, String componentType) {
        this.resourceUsedValuesDTO = resourceUsedValuesDTO;
        this.componentId = componentId;
        this.componentType = componentType;
        this.configurationBase = configuration;

    }

    public List<NotificationThreshold> getAllValues() {
        return List.of(getCpuValue(), getMemoryValue(), getSwapValue(), getDiskValue());
    }

    public NotificationThreshold getValue(BigDecimal actualValue, Integer threshold, String valueType) {
        return NotificationThreshold.builder().actualValue(actualValue).threshold(threshold)
                .componentId(componentId).valueType(valueType).componentType(componentType).build();
    }

    public NotificationThreshold getCpuValue() {
        return getValue(resourceUsedValuesDTO.usedCpuPercent,
                getValueOrOneHundred(configurationBase.usedCpuThreshold), "Cpu");
    }

    public NotificationThreshold getMemoryValue() {
        return getValue(resourceUsedValuesDTO.usedMemoryPercent,
                getValueOrOneHundred(configurationBase.usedMemoryThreshold), "Memory");
    }

    public NotificationThreshold getSwapValue() {
        return getValue(resourceUsedValuesDTO.usedSwapPercent,
                getValueOrOneHundred(configurationBase.usedSwapThreshold), "Swap");
    }

    public NotificationThreshold getDiskValue() {
        return getValue(resourceUsedValuesDTO.usedDiskPercent,
                getValueOrOneHundred(configurationBase.usedDiskThreshold), "RootFS");
    }

    private int getValueOrOneHundred(Integer value) {
        return Optional.ofNullable(value).orElse(100);
    }


}
