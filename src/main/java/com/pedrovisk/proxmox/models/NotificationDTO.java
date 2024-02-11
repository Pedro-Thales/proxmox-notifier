package com.pedrovisk.proxmox.models;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record NotificationDTO(String componentId, String componentType, String valueType,
                              BigDecimal threshold, BigDecimal actualValue){}
